package cn.kyc.dandanxia.module.member.utils.neo4j;

import cn.kyc.dandanxia.module.member.controller.app.resume.vo.AppResumeGraphRespVO;
import org.neo4j.driver.types.Node;
import java.util.*;

/**
 * 🌌 Neo4j 全息拓扑图谱通用清洗工具类
 */
public class Neo4jGraphUtils {

    /**
     * 将 Neo4jClient 的原始多维记录流，像素级脱水清洗为前端 ECharts 标准力导向图格式
     * @param rawResults Neo4jClient 查询返回的原始结果集
     * @param centerId 中心锚定球球的唯一ID (例如 "USER_32")
     * @return 满血版前端出参 VO
     */
    public static AppResumeGraphRespVO buildEchartsGraph(Collection<Map<String, Object>> rawResults, String centerId) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> links = new ArrayList<>();
        Set<String> visitedNodes = new HashSet<>(); // 核心去重挡板，防止球球物理重叠

        // 1. 兜底处理：如果图库里是空的，优雅地塞入一个基础中心球
        if (rawResults == null || rawResults.isEmpty()) {
            Map<String, Object> center = new HashMap<>();
            center.put("id", centerId);
            center.put("name", "未建立数据图谱");
            center.put("category", centerId.startsWith("USER_") ? "User" : "Job");
            center.put("symbolSize", 60);
            nodes.add(center);

            AppResumeGraphRespVO vo = new AppResumeGraphRespVO();
            vo.setNodes(nodes);
            vo.setLinks(links);
            return vo;
        }

        // 2. 纵深剥离 Neo4j 返回的因果载荷
        for (Map<String, Object> row : rawResults) {

            // =================================================================
            // A. 抓取并固化【中心原点节点】（人才 User 或 岗位 Job）
            // =================================================================
            Object uObj = row.get("u");
            if (uObj instanceof Node) {
                Node centerNode = (Node) uObj;
                if (visitedNodes.add(centerId)) {
                    Map<String, Object> node = new HashMap<>();
                    node.put("id", centerId);

                    Map<String, Object> props = centerNode.asMap();
                    String nodeName = props.containsKey("name") && props.get("name") != null
                            ? String.valueOf(props.get("name")) : String.valueOf(props.get("title"));

                    node.put("name", nodeName);
                    node.put("category", centerId.startsWith("USER_") ? "User" : "Job");
                    node.put("symbolSize", 60);
                    nodes.add(node);
                }
            }

            // =================================================================
            // B. 抓取并固化【一级关联节点】（Skill, Project, Award 标签等）
            // =================================================================
            Object targetObj = row.get("target");
            if (targetObj instanceof Node) {
                Node targetNode = (Node) targetObj;
                Map<String, Object> targetProps = targetNode.asMap();

                // 🌟 核心防御修复：Skill节点没有id属性，如果为null，无缝切向以 name 属性充当唯一图ID
                String targetId = targetProps.get("id") != null
                        ? targetProps.get("id").toString()
                        : (targetProps.get("name") != null ? targetProps.get("name").toString() : String.valueOf(targetNode.id()));

                String targetName = targetProps.get("name") != null ? targetProps.get("name").toString() : "未知节点";

                // 智能分类路由器
                String category = "Skill";
                if (targetId.startsWith("PROJ_")) {
                    category = "Project";
                } else if (targetId.contains("🏆") || targetId.contains("奖") || targetId.contains("杯") || targetId.startsWith("AWARD_")) {
                    category = "Award";
                }

                if (visitedNodes.add(targetId)) {
                    Map<String, Object> node = new HashMap<>();
                    node.put("id", targetId);
                    node.put("name", targetName);
                    node.put("category", category);
                    node.put("symbolSize", category.equals("Project") ? 45 : 35);
                    nodes.add(node);
                }

                // 编织中心球到一级节点之间的纽带连线
                Map<String, Object> link = new HashMap<>();
                link.put("source", centerId);
                link.put("target", targetId);
                link.put("value", "关联特征");
                if (!links.contains(link)) links.add(link);
            }

            // =================================================================
            // C. 抓取并固化【二级横向级联节点】 (如 Project -> USING_TECH -> Skill)
            // =================================================================
            Object subTargetObj = row.get("subTarget");
            if (targetObj instanceof Node && subTargetObj instanceof Node) {
                Node targetNode = (Node) targetObj;
                Node subTargetNode = (Node) subTargetObj;

                Map<String, Object> targetProps = targetNode.asMap();
                Map<String, Object> subTargetProps = subTargetNode.asMap();

                // 🌟 🌟 终极防御：全面采用安全防空震荡链，杜绝一切 .toString() 的闪爆现象
                String sourceId = targetProps.get("id") != null
                        ? targetProps.get("id").toString()
                        : (targetProps.get("name") != null ? targetProps.get("name").toString() : String.valueOf(targetNode.id()));

                String subTargetId = subTargetProps.get("id") != null
                        ? subTargetProps.get("id").toString()
                        : (subTargetProps.get("name") != null ? subTargetProps.get("name").toString() : String.valueOf(subTargetNode.id()));

                String subTargetName = subTargetProps.get("name") != null ? subTargetProps.get("name").toString() : "未知技术";

                if (visitedNodes.add(subTargetId)) {
                    Map<String, Object> node = new HashMap<>();
                    node.put("id", subTargetId);
                    node.put("name", subTargetName);
                    node.put("category", "Skill");
                    node.put("symbolSize", 35);
                    nodes.add(node);
                }

                // 建立项目与技术栈之间的横向连线
                Map<String, Object> subLink = new HashMap<>();
                subLink.put("source", sourceId);
                subLink.put("target", subTargetId);
                subLink.put("value", "核心技术组件");
                if (!links.contains(subLink)) links.add(subLink);
            }
        }

        // 3. 打包出厂
        AppResumeGraphRespVO vo = new AppResumeGraphRespVO();
        vo.setNodes(nodes);
        vo.setLinks(links);
        return vo;
    }
}
