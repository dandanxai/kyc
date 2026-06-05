import json

class ResumePrompts:
    """
    简历处理模块提示词
    """

    JOB_FAMILY_BUCKETS = {
        "TECH_DEV": "技术研发、计算机、软件、前后端开发、算法、数据工程等研发技术类",
        "DES_MEDIA": "视觉设计、UI/UX、新媒体、视频剪辑、动漫插画等创意视觉类",
        "BIZ_SALES": "销售、市场营销、商务拓展、大客户经理、渠道开拓类",
        "ADMIN_HR": "行政文职、人力资源、法务、前台接待、数据录入类",
        "FIN_REVENUE": "财务、会计、审计、风控、金融交易、证券投资类",
        "OPERATIONS": "运营、产品经理、活动策划、用户运营、数据分析类",
        "CATERING_LEISURE": "传统餐饮、后厨、切配、厨师长、生活服务与休闲旅居类",
        "LOGISTICS_WMS": "物流快递、仓储管理、WMS系统操作、供应链配送类",
        "MANUFACTURING": "传统制造、电子工程、机械自动化、工厂生产管理类",
        "TRADING_GLOBAL": "外贸、跨境电商、国际物流、跟单报关、海外仓储类",
        "MEDICAL_HEALTH": "医疗护理、医药研发、医疗器械、健康管理、生物制药类",
        "EDU_CONSULT": "教育培训、教师、课程顾问、心理咨询、专业顾问类",
        "OTHER": "上述分类均无法包含的其余生僻或边缘行业岗位（兜底分类）"
    }

    INDUSTRY_BUCKETS = {
        "INTERNET_AI_SOFTWARE": "互联网、人工智能、大模型、软件开发、信息安全、Saas服务",
        "FINANCE_BANKING": "银行、证券、保险、金融科技（FinTech）、基金投资",
        "MANUFACTURING_AUTO": "汽车制造、新能源汽车、重工业、机械制造、半导体芯片",
        "BIOMEDICAL_HEALTH": "生物医药、医疗器械、医院临床、健康产业",
        "CONSUMER_RETAIL": "快消品（FMCG）、传统零售、电商平台、服装美妆",
        "REAL_ESTATE_CONSTRUCT": "房地产开发、建筑工程、物业管理、空间设计",
        "EDUCATION_TRAINING": "高等教育、职业教育、在线教育、教辅机构",
        "CULTURE_ENTERTAINMENT": "影视传媒、游戏动漫、文化旅游、体育竞技",
        "LOGISTICS_SUPPLY_CHAIN": "仓储物流、海运空运、供应链管理、货运代理",
        "ENERGY_CHEMICAL": "传统能源、石油化工、光伏储能、环保低碳",
        "OTHER": "其余无法明确归类的边缘或传统混合行业（兜底分类）"
    }

    # 🌟 升级：直接分析提取出来的简历纯文本内容
    PARSE_TEMPLATE = """
        你是一个拥有10年以上经验的高阶大厂HR系统专家，专门负责将简历转化为标准人画像 JSON。

        待解析简历原始文本内容如下：
        --------------------------------------------------
        __RESUME_CONTENT_PLACEHOLDER__
        --------------------------------------------------

        【分类与逻辑约束】
        1. **work_experience (工作经验) 判定准则**：
           - 严禁将项目经历误判为工作经验。
           - 仅在简历中出现明确的“入职”、“公司聘用”、“实习生职位”等字样，且该项目被描述为是雇佣方的正式业务时，才计入工作经验。
           - 如果项目只是个人开发、校内工作室产出、或者没有明确雇佣合同关系，必须放入 project_experience，work_experience 必须为 []。
        2. 岗位/行业分类：严格参考下列字典 Key 值。

        - 岗位职能大类: """ + str(list(JOB_FAMILY_BUCKETS.keys())) + """
        - 行业赛道大类: """ + str(list(INDUSTRY_BUCKETS.keys())) + """

        【输出格式强约束】
        返回标准 JSON，不要包含 markdown 标记。

        {
          "name": "姓名",
          "phone": "手机号",
          "email": "邮箱",
          "gender": "性别",
          "age": 0,
          "current_city": "当前城市",
          "expected_city": "期望城市",
          "expected_position": "期望岗位",
          "expected_salary_min": 0,
          "expected_salary_max": 0,
          "availability": "到岗时间",
          "highest_education": "最高学历",
          "school": "学校",
          "major": "专业",
          "graduation_year": 0,
          "years_of_experience": "工作/项目经验年限",
          "work_experience": [
            {
              "company": "公司名称，若无明确雇佣公司则留空",
              "position": "担任职位",
              "start_time": "开始时间",
              "end_time": "结束时间",
              "description": "职责描述"
            }
          ],
          "project_experience": [
            {
              "name": "项目名称",
              "role": "担任角色",
              "technology": "技术栈",
              "description": "项目简介及产出"
            }
          ],
          "core_skills": ["核心技能"],
          "skill_tags": ["框架与工具"],
          "certifications": ["证书"],
          "awards": ["奖项"],
          "achievements": "重大成果",
          "self_evaluation": "自我评价",
          "job_family_bucket": "岗位大类KEY",
          "industry_bucket": "行业大类KEY"
        }
        """

class JobPrompts:
    """岗位/职位处理模块提示词"""
    ANALYZE_TEMPLATE = "..."

class MatchPrompts:
    """人岗智能匹配模块提示词"""
    MATCH_SCORE_TEMPLATE = "..."