import json
from app.config.settings import settings
# 🌟 引入你写好的高级数据库工具类（确保你的 db_util.py 路径正确，比如在 app.utils.db_util 或者是 app.dao.db_util）
from app.utils.db_util import DBUtil


class ResumeDao:
    """
    专门针对 KYC_RESUME 表的数据访问对象 (DAO)
    """

    @classmethod
    def update_parsed_result(cls, resume_id: int, parsed_data: dict) -> bool:
        """
        将大模型解析出的字典数据，更新到达梦数据库中
        """
        # 1. 提取用于“前端快速渲染”的字段（处理成字符串）
        # 🟢 修正：转成标准的 ["Spring Boot", "Redis"] 格式 JSON 字符串数组
        skill_tags_json = json.dumps(parsed_data.get("skill_tags", []), ensure_ascii=False)
        achievements_str = parsed_data.get("achievements", "")

        # 2. 提取用于“硬过滤”的基础字段
        name = parsed_data.get("name", "")
        phone = parsed_data.get("phone", "")
        email = parsed_data.get("email", "")
        job_family = parsed_data.get("job_family_bucket", "")
        industry = parsed_data.get("industry_bucket", "")
        city = parsed_data.get("expected_city", "")
        edu = parsed_data.get("highest_education", "")

        # 处理可能为数字的字段（兜底防止 AI 返回空字符串或非数字）
        try:
            years_exp = float(parsed_data.get("years_of_experience", 0.0))
        except:
            years_exp = 0.0

        try:
            grad_year = int(parsed_data.get("graduation_year", 0))
        except:
            grad_year = 0

        # 3. 把全量数据打包成 JSON 字符串，用来兜底
        full_json_str = json.dumps(parsed_data, ensure_ascii=False)

        # 4. 执行达梦数据库更新 (因为 DBUtil 里面已经执行了 SET SCHEMA KYC，所以这里直接写表名 kyc_resume 即可)
        sql = """
            UPDATE kyc_resume 
            SET 
                parse_status = 2,
                parsed_json = :json_str,
                skill_tags = :skills,
                achievements = :achieves,
                name = :name,
                phone = :phone,
                email = :email,
                job_family_bucket = :job_family,
                industry_bucket = :industry,
                expected_city = :city,
                highest_education = :edu,
                years_of_experience = :years,
                graduation_year = :grad,
                update_time = CURRENT_TIMESTAMP
            WHERE id = :resume_id
        """

        params = {
            "json_str": full_json_str,
            "skills": skill_tags_json,
            "achieves": achievements_str,
            "name": name,
            "phone": phone,
            "email": email,
            "job_family": job_family,
            "industry": industry,
            "city": city,
            "edu": edu,
            "years": years_exp,
            "grad": grad_year,
            "resume_id": resume_id
        }

        try:
            # 🌟 核心优化：直接借用你写好的 DBUtil 上下文管理器，安全、自动提交、自动回收
            with DBUtil.connection_context() as conn:
                cursor = conn.cursor()
                cursor.execute(sql, params)
                cursor.close() # 及时释放游标

            print(f"💾 [数据持久化] 简历 ID: {resume_id} 已通过 DBUtil 成功写入达梦数据库！")
            return True

        except Exception as e:
            print(f"❌ [数据持久化] 写入达梦数据库发生业务崩溃: {str(e)}")
            raise e