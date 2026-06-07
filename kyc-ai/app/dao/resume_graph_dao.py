import json
import re
from app.utils.neo4j_util import Neo4jUtil


class ResumeGraphDao:
    """
    专门针对 Neo4j 图数据库的简历全息拓扑关系网络数据访问对象 (DAO)
    """

    @classmethod
    def sync_resume_to_graph(cls, resume_id: int, parsed_data: dict) -> bool:
        """
        核心高能：将大模型解析数据按照「独立简历个体」和「双轨对称树状」规矩固化入图
        """
        # 🌟 绝杀主键隔离：使用 RESUME_ID 充当中心点，彻底解决一人多简历的覆盖冲突
        resume_graph_id = f"RESUME_{resume_id}"

        # 1. 提取并合并技术标签
        skills = parsed_data.get("skill_tags", [])
        core_skills = parsed_data.get("core_skills", [])
        if not isinstance(skills, list): skills = []
        if not isinstance(core_skills, list): core_skills = []
        all_skills = list(set(skills + core_skills))

        # 2. 提取荣誉奖项
        awards = parsed_data.get("awards", [])
        if not isinstance(awards, list): awards = []

        # 3. 提取项目经验并处理内部技术栈
        raw_projects = parsed_data.get("project_experience", [])
        if not isinstance(raw_projects, list): raw_projects = []

        processed_projects = []
        for proj in raw_projects:
            proj_name = proj.get("name", "").strip()
            if not proj_name:
                continue

            tech_data = proj.get("technology", "")
            tech_tags = []
            if isinstance(tech_data, list):
                tech_tags = [str(t).strip() for t in tech_data if str(t).strip()]
            elif isinstance(tech_data, str) and tech_data.strip():
                tech_tags = [t.strip() for t in re.split(r'[,，、/| \t]+', tech_data) if t.strip()]

            processed_projects.append({
                "name": proj_name,
                "role": proj.get("role", "开发者"),
                "description": proj.get("description", ""),
                "tech_tags": tech_tags
            })

        # 4. 双轨树状对称 Cypher 织网引擎
        cypher_mesh = """
        // ==================== A. 描摹独立的简历核心原点 ====================
        MERGE (r:Resume {id: $resumeGraphId})
        SET r.name = $name, r.school = $school, r.major = $major, r.highest_education = $edu

        // ==================== B. 打造 4 大核心骨架树干节点 ====================
        MERGE (catFront:Category {id: "CAT_" + $resumeId + "_FRONT", name: "前端技术"})
        MERGE (catBack:Category {id: "CAT_" + $resumeId + "_BACK", name: "后端技术"})
        MERGE (catProj:Category {id: "CAT_" + $resumeId + "_PROJ", name: "实战项目"})
        MERGE (catAward:Category {id: "CAT_" + $resumeId + "_AWARD", name: "重磅荣誉"})

        // 骨架树干挂载到当前简历节点上
        MERGE (r)-[:HAS_TRACK]->(catFront)
        MERGE (r)-[:HAS_TRACK]->(catBack)
        MERGE (r)-[:HAS_TRACK]->(catProj)
        MERGE (r)-[:HAS_TRACK]->(catAward)

        // ==================== C. 技能栈分流挂载 ====================
        FOREACH (skillName IN $allSkills |
            MERGE (s:Skill {name: skillName})
            FOREACH (_ IN CASE WHEN 
                toLower(skillName) CONTAINS 'vue' OR 
                toLower(skillName) CONTAINS 'uniapp' OR 
                toLower(skillName) CONTAINS 'element' OR 
                toLower(skillName) CONTAINS 'echarts' OR 
                toLower(skillName) CONTAINS 'datav' OR 
                toLower(skillName) CONTAINS 'axios' OR 
                toLower(skillName) CONTAINS 'css' OR 
                toLower(skillName) CONTAINS 'js' 
                THEN [1] ELSE [] END |
                MERGE (catFront)-[:CONTAINS_SKILL]->(s)
            )
            FOREACH (_ IN CASE WHEN NOT (
                toLower(skillName) CONTAINS 'vue' OR 
                toLower(skillName) CONTAINS 'uniapp' OR 
                toLower(skillName) CONTAINS 'element' OR 
                toLower(skillName) CONTAINS 'echarts' OR 
                toLower(skillName) CONTAINS 'datav' OR 
                toLower(skillName) CONTAINS 'axios' OR 
                toLower(skillName) CONTAINS 'css' OR 
                toLower(skillName) CONTAINS 'js'
            ) THEN [1] ELSE [] END |
                MERGE (catBack)-[:CONTAINS_SKILL]->(s)
            )
        )

        // ==================== D. 实战项目挂载与横向跨维度级联 ====================
        FOREACH (proj IN $projects |
            MERGE (p:Project {id: "PROJ_RESUME_" + $resumeId + "_" + proj.name, name: proj.name})
            SET p.role = proj.role
            MERGE (catProj)-[:CONTAINS_PROJ]->(p)

            FOREACH (techName IN proj.tech_tags |
                MERGE (ts:Skill {name: techName})
                MERGE (p)-[:USING_TECH]->(ts)
            )
        )

        // ==================== E. 重磅荣誉挂载 ====================
        FOREACH (awardName IN $awards |
            MERGE (a:Award {name: awardName})
            MERGE (catAward)-[:CONTAINS_AWARD]->(a)
        )
        """

        try:
            with Neo4jUtil.session_context() as session:
                session.run(
                    cypher_mesh,
                    resumeGraphId=resume_graph_id,
                    resumeId=str(resume_id),
                    name=parsed_data.get("name", "未知"),
                    school=parsed_data.get("school", "未知"),
                    major=parsed_data.get("major", "未知"),
                    edu=parsed_data.get("highest_education", "未知"),
                    allSkills=all_skills,
                    projects=processed_projects,
                    awards=awards
                )
            print(f"🌌 [Neo4j 织网大成功] 简历 ID: {resume_id} 的独立双轨规整网已精准落盘！")
            return True
        except Exception as e:
            print(f"❌ [Neo4j 织网失败] 写入崩溃，原因: {str(e)}")
            return False