package cn.kyc.dandanxia.module.member.controller.app.resume.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AppResumeGraphRespVO {
    // 🌟 承载所有的彩色气泡节点 (包含 id, name, label, size 等属性)
    private List<Map<String, Object>> nodes;

    // 🌟 承载所有气泡中间的拖拽引力连线 (包含 source, target, type 等属性)
    private List<Map<String, Object>> links;
}
