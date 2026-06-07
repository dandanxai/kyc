package cn.kyc.dandanxia.module.member.utils.mq.producer;

import cn.kyc.dandanxia.module.member.utils.mq.message.PositionParseSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * 岗位招聘模块相关消息生产者 - 严格遵循芋道 RabbitMQ 手册规范
 * * @author 黄胜
 */
@Slf4j
@Component
public class PositionProducer {

    @Resource
    private RabbitTemplate rabbitTemplate; // 🎯 注入原生的 RabbitTemplate 对象

    /**
     * 投递岗位招聘需求文档解析任务消息
     *
     * @param id       岗位自增主键ID
     * @param filePath 岗位需求文档的 OSS 相对/绝对路径
     * @param userId   发布岗位的企业用户ID（用于 SSE 实时推流对齐）
     */
    public void sendPositionParseMessage(Long id, String filePath, Long userId) {
        // 1. 镜像组装消息体
        PositionParseSendMessage message = new PositionParseSendMessage()
                .setId(id)
                .setFilePath(filePath)
                .setUserId(userId);

        // 2. 🎯 利用专属的 B 端队列名称直接发送消息
        log.info("[sendPositionParseMessage][开始投递岗位招聘文档解析消息，岗位ID:({})]", id);
        rabbitTemplate.convertAndSend(PositionParseSendMessage.QUEUE, message);
    }
}
