package cn.kyc.dandanxia.module.member.utils.mq.message;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * 🚀 核心新增：简历图谱高维织网同步消息体 - 严格遵循芋道 RabbitMQ 手册规范
 */
@Data
@Accessors(chain = true) // 方便链式调用
public class ResumeGraphSyncMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    // 🎯 规约：对应你在 RabbitMQ 后台创建的独立图谱同步队列
    public static final String QUEUE = "KYC_RESUME_GRAPH_SYNC_QUEUE";

    /**
     * 简历档案主键ID（对应达梦自增主键）
     */
    private Long id;

    /**
     * 大模型解析成功后的全量标准 JSON 字符串（直接把大模型吐出的全量 JSON 砸过去，让 Python 消费端解析）
     */
    private String parsedJsonStr;
}
