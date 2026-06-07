import json
from app.config.settings import settings
# 🌟 100% 引入你写好的高级达梦数据库工具类，确保路径与上下文规范完全一致
from app.utils.db_util import DBUtil


class PositionDao:
    """
    专门针对 KYC_POSITION 表的数据访问对象 (DAO)
    """

    @classmethod
    def update_parsed_result(cls, position_id: int, parsed_data: dict) -> bool:
        """
        将大模型解析出的岗位字典数据，精准更新到达梦数据库中
        """
        # 1. 提取用于“前端快速渲染”的标签字段
        # 🟢 严格对齐：转成标准的 ["Java", "MySQL"] 格式 JSON 字符串数组，存入相应的 VARCHAR 字段中
        required_skills_json = json.dumps(parsed_data.get("required_skills", []), ensure_ascii=False)
        keywords_json = json.dumps(parsed_data.get("keywords", []), ensure_ascii=False)

        # 2. 提取用于两端高效“硬过滤”的基础字段（严格对应大模型返回的 B 端 JSON 键名）
        job_family = parsed_data.get("job_family_bucket", "")
        industry = parsed_data.get("industry_bucket", "")
        city = parsed_data.get("city", "")
        edu_required = parsed_data.get("education_required", "")

        # 3. 薪资纯数字提取（严格对齐简历端，存入真实元/月的薪资数字，如 15000）
        try:
            salary_min = int(parsed_data.get("salary_min", 0))
        except:
            salary_min = 0

        try:
            salary_max = int(parsed_data.get("salary_max", 0))
        except:
            salary_max = 0

        # 4. 最低经验年限转换（兜底防止 AI 返回文字描述导致 DECIMAL 转换崩溃）
        # 从 "experience_required" 获取内容，默认提取或兜底设定
        exp_desc = parsed_data.get("experience_required", "")
        try:
            # 简单正则提取数字：比如 "1-3年" -> 1.0；"应届生" -> 0.0；"3年以上" -> 3.0
            match = re.search(r'\d+', exp_desc)
            years_exp_min = float(match.group()) if match else 0.0
        except:
            years_exp_min = 0.0

        # 5. 把全量数据打包成 JSON 字符串，用来填入 CLOB 兜底字段
        full_json_str = json.dumps(parsed_data, ensure_ascii=False)

        # 6. 执行达梦数据库更新 (直接写表名 kyc_position 即可)
        sql = """
            UPDATE kyc_position 
            SET 
                parse_status = 2,
                parsed_json = :json_str,
                required_skills = :skills,
                keywords = :keywords,
                job_family_bucket = :job_family,
                industry_bucket = :industry,
                city = :city,
                education_required = :edu,
                years_of_experience_min = :years,
                salary_min = :salary_min,
                salary_max = :salary_max,
                update_time = CURRENT_TIMESTAMP
            WHERE id = :position_id
        """

        params = {
            "json_str": full_json_str,
            "skills": required_skills_json,
            "keywords": keywords_json,
            "job_family": job_family,
            "industry": industry,
            "city": city,
            "edu": edu_required,
            "years": years_exp_min,
            "salary_min": salary_min,
            "salary_max": salary_max,
            "position_id": int(position_id) # 强转 int 防止长整型映射溢出
        }

        try:
            # 🌟 核心优化：直接借用你写好的 DBUtil 上下文管理器，安全、自动提交、自动回收
            with DBUtil.connection_context() as conn:
                cursor = conn.cursor()
                cursor.execute(sql, params)
                cursor.close() # 及时释放游标

            print(f"💾 [数据持久化] 岗位 ID: {position_id} 已通过 DBUtil 成功写入达梦数据库！")
            return True

        except Exception as e:
            print(f"❌ [数据持久化] 写入达梦数据库发生业务崩溃: {str(e)}")
            raise e