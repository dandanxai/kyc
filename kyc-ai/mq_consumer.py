import json
import time
import pika
import functools
from concurrent.futures import ThreadPoolExecutor
from app.config.settings import settings
from app.services.parser_resume_service import ParserResumeService

# 🌟 核心引擎：创建一个最大容纳 10 个工位的线程池
# 这意味着系统可以同时、并行地解析最多 10 份简历！
executor = ThreadPoolExecutor(max_workers=10)


# ==================== 1. 线程安全的 ACK 回调 ====================
def ack_message(ch, delivery_tag):
    """主线程执行的 ACK 确认"""
    if ch.is_open:
        ch.basic_ack(delivery_tag)
        print(f"✅ [MQ 确认] 成功应答，已将任务移出队列 (Tag: {delivery_tag})")


def nack_message(ch, delivery_tag):
    """主线程执行的 NACK 丢弃"""
    if ch.is_open:
        ch.basic_nack(delivery_tag, requeue=False)
        print(f"❌ [MQ 丢弃] 任务失败，已丢弃死信队列 (Tag: {delivery_tag})")


# ==================== 2. 子线程的真正苦力活 ====================
def background_parsing_task(connection, ch, delivery_tag, task_data):
    """
    这是由线程池里的子线程独立运行的方法，耗时再长也不会阻塞 MQ 接收新消息！
    """
    try:
        # 1. 调取你的 Service，开始高强度耗时解析（下载OSS + 调AI）
        # 这里的结果只是打印，不会写入数据库
        success = ParserResumeService.process_task(task_data)

        # 2. ⚠️ Pika 不是线程安全的！子线程不能直接发送 Ack！
        # 必须把 Ack 指令打包，塞回给 connection 的主线程事件循环中去执行
        if success:
            cb = functools.partial(ack_message, ch, delivery_tag)
        else:
            cb = functools.partial(nack_message, ch, delivery_tag)

        connection.add_callback_threadsafe(cb)

    except Exception as e:
        print(f"🔥 [子线程崩溃] 简历解析致命错误: {str(e)}")
        cb = functools.partial(nack_message, ch, delivery_tag)
        connection.add_callback_threadsafe(cb)


# ==================== 3. 主线程的高速分发台 ====================
def on_message_callback(connection, ch, method, properties, body):
    """
    主线程只干一件事：接单！接到单子立刻扔给子线程，绝不停留！
    """
    try:
        message_str = body.decode('utf-8')
        task_data = json.loads(message_str)
        resume_id = task_data.get("id", "未知ID")

        print(f"\n📩 [主线程接单] 收到简历 ID: {resume_id} (Tag: {method.delivery_tag})")
        print(f"⚡ [派发任务] 主线程秒甩锅，已将任务 {resume_id} 投递给空闲子线程...")

        # 将干活任务扔给线程池异步执行
        executor.submit(background_parsing_task, connection, ch, method.delivery_tag, task_data)

    except Exception as e:
        print(f"❌ [主线程解析消息体失败]: {str(e)}")
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)


# ==================== 4. 消费者启动器 ====================
def start_test_consumer():
    credentials = pika.PlainCredentials(settings.rabbitmq_user, settings.rabbitmq_password)
    parameters = pika.ConnectionParameters(
        host=settings.rabbitmq_host,
        port=settings.rabbitmq_port,
        credentials=credentials,
        heartbeat=60,
        blocked_connection_timeout=300
    )

    while True:
        try:
            print(f"📡 [正在连接] 正在尝试连接至阿里云：{settings.rabbitmq_host}:{settings.rabbitmq_port} ...")
            connection = pika.BlockingConnection(parameters)
            channel = connection.channel()

            channel.queue_declare(queue=settings.queue_name, durable=True)

            # 🌟 核心改造：放宽限流，允许主线程一次性囤积 10 个未 Ack 的任务发给子线程
            channel.basic_qos(prefetch_count=10)

            # 巧妙利用 Python 闭包，将 connection 对象传给 callback
            def callback_wrapper(ch, method, properties, body):
                on_message_callback(connection, ch, method, properties, body)

            channel.basic_consume(
                queue=settings.queue_name,
                on_message_callback=callback_wrapper,
                auto_ack=False
            )

            print(f"🚀 [多线程引擎就绪] 你的 Python 已经成功挂载到 RabbitMQ！")
            print(f"🔥 [并发容量]: 当前支持最高 10 份简历同时在线解析。")
            print(f"📡 正在死循环监听队列: 【{settings.queue_name}】...")
            channel.start_consuming()

        except pika.exceptions.AMQPConnectionError:
            print("⚠️ [连接断开] 无法建立连接，10秒后尝试自动重连...")
            time.sleep(10)
        except KeyboardInterrupt:
            print("\n🛑 [手动退出] 正在关闭消费者，等待线程池收尾...")
            executor.shutdown(wait=False)
            break
        except Exception as e:
            print(f"💥 [未知错误]: {str(e)}，10秒后重试...")
            time.sleep(10)


if __name__ == "__main__":
    start_test_consumer()