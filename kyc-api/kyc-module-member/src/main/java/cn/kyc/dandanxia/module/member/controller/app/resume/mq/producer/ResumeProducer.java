package cn.kyc.dandanxia.module.member.controller.app.resume.mq.producer;

import cn.kyc.dandanxia.module.member.controller.app.resume.mq.message.ResumeParseSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * 简历模块相关消息生产者 - 严格遵循芋道 RabbitMQ 手册规范
 */
@Slf4j
@Component
public class ResumeProducer {

    @Resource
    private RabbitTemplate rabbitTemplate; // 🎯 重点：注入原生的 RabbitTemplate 对象

    /**
     * 投递简历解析任务消息
     *
     * @param id 简历自增主键ID
     * @param filePath 简历的 OSS 相对/绝对路径
     * @param userId 当前上传的用户ID
     */
    public void sendResumeParseMessage(Long id, String filePath, Long userId) {
        // 1. 组装消息体
        ResumeParseSendMessage message = new ResumeParseSendMessage()
                .setId(id)
                .setFilePath(filePath)
                .setUserId(userId);

        // 2. 🎯 重点：利用队列名称直接发送消息
        log.info("[sendResumeParseMessage][开始投递简历解析消息，简历ID:({})]", id);
        rabbitTemplate.convertAndSend(ResumeParseSendMessage.QUEUE, message);
    }
}