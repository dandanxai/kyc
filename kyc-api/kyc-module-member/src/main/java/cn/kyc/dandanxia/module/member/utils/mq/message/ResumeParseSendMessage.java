package cn.kyc.dandanxia.module.member.utils.mq.message;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * 简历大模型解析消息体 - 严格遵循芋道 RabbitMQ 手册规范
 */
@Data
@Accessors(chain = true) // 方便链式调用
public class ResumeParseSendMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    // 🎯 重点：定义对应我们在 Web 后台手动创建的 Queue 名称
    public static final String QUEUE = "KYC_RESUME_PARSE_QUEUE";

    /**
     * 简历档案主键ID
     */
    private Long id;

    /**
     * 文件存储路径/OSS URL
     */
    private String filePath;

    /**
     * 当前登录用户ID
     */
    private Long userId;
}
