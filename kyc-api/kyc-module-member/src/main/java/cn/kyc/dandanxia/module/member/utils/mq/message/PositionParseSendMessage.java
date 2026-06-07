package cn.kyc.dandanxia.module.member.utils.mq.message;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * 岗位招聘档案解析消息体 - 严格对称 C 端设计
 * * @author 黄胜
 */
@Data
@Accessors(chain = true)
public class PositionParseSendMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 🎯 专属 B 端岗位招聘文档解析的 RabbitMQ 队列名称
     */
    public static final String QUEUE = "KYC_POSITION_PARSE_QUEUE";

    /**
     * 岗位自增主键ID
     */
    private Long id;

    /**
     * 岗位需求文档的 OSS 相对/绝对路径
     */
    private String filePath;

    /**
     * 关联的企业ID/用户ID（用于后续精确推回 SSE 捷报）
     */
    private Long userId;

}
