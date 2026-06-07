import json
import time
import pika
import functools
import requests
from concurrent.futures import ThreadPoolExecutor
from app.config.settings import settings
from app.services.parser_resume_service import ParserResumeService
from app.services.parser_position_service import ParserPositionService
from app.dao.resume_graph_dao import ResumeGraphDao  # 🌟 引入改好的图谱组件

# 扩容线程池工位，三大队列同开并发无忧
executor = ThreadPoolExecutor(max_workers=20)

# 🌟 核心定义：对齐 Java 端在 MQ 消息体里硬编码的独立图谱队列钢印
RESUME_GRAPH_SYNC_QUEUE = "KYC_RESUME_GRAPH_SYNC_QUEUE"


def ack_message(ch, delivery_tag):
    if ch.is_open:
        ch.basic_ack(delivery_tag)
        print(f"✅ [MQ 应答] 任务移出队列 (Tag: {delivery_tag})")


def nack_message(ch, delivery_tag):
    if ch.is_open:
        ch.basic_nack(delivery_tag, requeue=False)
        print(f"❌ [MQ 丢弃] 任务失败并移入死信队列 (Tag: {delivery_tag})")


# ==================== 🛠️ 核心新增：专门干图谱织网苦力活的子线程 ====================
def background_graph_sync_task(connection, ch, delivery_tag, task_data):
    """
    独立子线程：当 Java 查不到图谱、按需发送异步请求时，专门负责在这里安安静静地织网
    """
    try:
        resume_id = task_data.get("id")
        # 抓取 Java 丢过来的全量 parsedJsonStr 并火速还原为 Python 字典
        raw_json_str = task_data.get("parsedJsonStr", "{}")
        parsed_json = json.loads(raw_json_str)

        print(f"🌌 [子线程接单] 开始为独立简历 ID: {resume_id} 进行全向跨维度高维对称织网...")

        # 调用新版图谱 DAO
        success = ResumeGraphDao.sync_resume_to_graph(resume_id, parsed_json)

        # 🌟 绝杀合围：织网大成功后，反向炮轰 Java 的通知信道，打上 RESUME_GRAPH 钢印，让前端关闭 Loading 直出！
        if success:
            try:
                # 动态获取当初大模型任务相关的 userId（如果没有则兜底）
                user_id = parsed_json.get("userId", 1)
                notify_url = "http://127.0.0.1:48080/app-api/member/sse/notify-success"
                payload = {
                    "userId": int(user_id),
                    "dataId": str(resume_id),
                    "taskType": "RESUME_GRAPH"  # 👈 吹响图谱画完的专属号子
                }
                res = requests.post(notify_url, json=payload, timeout=5)
                print(f"📡 [📡图谱通知] 长连接广播已发射，Java 响应: {res.status_code}")
            except Exception as ne:
                print(f"⚠️ [📡图谱通知] 发射 SSE 喜报时网络受阻: {str(ne)}")

        # 打包应答
        cb = functools.partial(ack_message, ch, delivery_tag) if success else functools.partial(nack_message, ch,
                                                                                                delivery_tag)
        connection.add_callback_threadsafe(cb)

    except Exception as e:
        print(f"🔥 [图谱子线程崩溃] 织网线程致命暴击: {str(e)}")
        cb = functools.partial(nack_message, ch, delivery_tag)
        connection.add_callback_threadsafe(cb)


# ==================== 原有的解析任务分发枢纽不变 ====================
def background_parsing_task(connection, ch, delivery_tag, task_data, task_type: str):
    try:
        task_data["taskType"] = task_type
        if task_type == "RESUME":
            success = ParserResumeService.process_task(task_data)
        elif task_type == "POSITION":
            success = ParserPositionService.process_task(task_data)
        else:
            success = False

        cb = functools.partial(ack_message, ch, delivery_tag) if success else functools.partial(nack_message, ch,
                                                                                                delivery_tag)
        connection.add_callback_threadsafe(cb)
    except Exception as e:
        print(f"🔥 [子线程崩溃] {task_type} 异步解析线程发生致命暴击: {str(e)}")
        cb = functools.partial(nack_message, ch, delivery_tag)
        connection.add_callback_threadsafe(cb)


def on_message_callback(connection, ch, method, properties, body, task_type: str):
    try:
        message_str = body.decode('utf-8')
        task_data = json.loads(message_str)
        task_id = task_data.get("id", "未知ID")

        print(f"\n📩 [主线程接单] 🚀 抓到【{task_type}】任务 ID: {task_id} (Tag: {method.delivery_tag})")

        if task_type == "GRAPH_SYNC":
            # 分流到专门的图谱同步后台苦力线程
            executor.submit(background_graph_sync_task, connection, ch, method.delivery_tag, task_data)
        else:
            executor.submit(background_parsing_task, connection, ch, method.delivery_tag, task_data, task_type)

    except Exception as e:
        print(f"❌ [主线程解析消息体失败]: {str(e)}")
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)


# ==================== 消费者集群启动中心 ====================
def start_test_consumer():
    credentials = pika.PlainCredentials(settings.rabbitmq_user, settings.rabbitmq_password)
    parameters = pika.ConnectionParameters(
        host=settings.rabbitmq_host, credentials=credentials, heartbeat=60, blocked_connection_timeout=300
    )

    while True:
        try:
            print(f"📡 [正在连接] 正在挂载至 RabbitMQ 服务端：{settings.rabbitmq_host} ...")
            connection = pika.BlockingConnection(parameters)
            channel = connection.channel()

            # 宣告并维持 3 大独立队列安全线
            channel.queue_declare(queue=settings.resume_queue_name, durable=True)
            channel.queue_declare(queue=settings.position_queue_name, durable=True)
            channel.queue_declare(queue=RESUME_GRAPH_SYNC_QUEUE, durable=True)  # 🌟 核心新增

            channel.basic_qos(prefetch_count=20)

            # 🚀 订阅 1：简历常规解析
            channel.basic_consume(
                queue=settings.resume_queue_name,
                on_message_callback=lambda ch, m, p, b: on_message_callback(connection, ch, m, p, b, "RESUME")
            )

            # 🚀 订阅 2：岗位常规解析
            channel.basic_consume(
                queue=settings.position_queue_name,
                on_message_callback=lambda ch, m, p, b: on_message_callback(connection, ch, m, p, b, "POSITION")
            )

            # 🚀 订阅 3：🌟 核心新增：按需延迟图谱编织
            channel.basic_consume(
                queue=RESUME_GRAPH_SYNC_QUEUE,
                on_message_callback=lambda ch, m, p, b: on_message_callback(connection, ch, m, p, b, "GRAPH_SYNC")
            )

            print(f"🚀 [全能多线程三引擎全挂载就绪！]")
            print(f"📡 监听 A (常规简历): 【{settings.resume_queue_name}】")
            print(f"📡 监听 B (常规岗位): 【{settings.position_queue_name}】")
            print(f"📡 监听 C (延迟图谱): 【{RESUME_GRAPH_SYNC_QUEUE}】...")

            channel.start_consuming()

        except pika.exceptions.AMQPConnectionError:
            print("⚠️ [连接断开] 失去信号，10秒后尝试自动重连...")
            time.sleep(10)
        except KeyboardInterrupt:
            executor.shutdown(wait=False)
            break
        except Exception as e:
            print(f"💥 [系统暴击]: {str(e)}，10秒后重启事件循环...")
            time.sleep(10)


if __name__ == "__main__":
    start_test_consumer()