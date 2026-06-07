package cn.kyc.dandanxia.module.member.utils.mq.producer;

import cn.kyc.dandanxia.module.member.utils.mq.message.ResumeGraphSyncMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * 🚀 核心新增：简历图谱同步消息生产者 - 严格遵循芋道 RabbitMQ 手册规范
 */
@Slf4j
@Component
public class ResumeGraphProducer {

    @Resource
    private RabbitTemplate rabbitTemplate; // 注入原生的 RabbitTemplate 对象

    /**
     * 投递简历图谱双轨对称织网同步消息
     *
     * @param id 简历自增主键ID
     * @param parsedJsonStr 大模型解析后的标准全量 JSON 字符串
     */
    public void sendGraphSyncMessage(Long id, String parsedJsonStr) {
        // 1. 组装消息体
        ResumeGraphSyncMessage message = new ResumeGraphSyncMessage()
                .setId(id)
                .setParsedJsonStr(parsedJsonStr);

        // 2. 利用队列名称直接发送消息
        log.info("[sendGraphSyncMessage][触发高维图谱引擎，开始投递图谱同步消息，简历ID:({})]", id);
        rabbitTemplate.convertAndSend(ResumeGraphSyncMessage.QUEUE, message);
    }
}
