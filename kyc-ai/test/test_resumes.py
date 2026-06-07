# -*- coding: utf-8 -*-

test_resumes_data = [
    {
        "resume_id": "32",
        "name": "黄胜", "highest_education": "大专", "major": "软件技术", "years_of_experience": "3年",
        "expected_position": "Java全栈开发工程师", "expected_city": "合肥",
        "expected_salary_min": 8000, "expected_salary_max": 12000,
        "job_family_bucket": "BACKEND", "industry_bucket": "IT_SOFTWARE",
        "core_skills": ["Java", "Spring Boot", "MyBatis-Plus", "MySQL", "Redis"],
        "skill_tags": ["Vue3", "UniApp", "Element Plus", "ECharts"],
        "project_experience": [
            {
                "name": "凌云智控全栈平台", "technology": "Spring Boot, Vue2, MySQL, Redis, ECharts",
                "description": "基于若依框架二次开发的物联网管理平台，利用 Redis 缓存热点设备数据，降低达梦数据库压力。"
            }
        ]
    },
    {
        "resume_id": "902",
        "name": "张推拿", "highest_education": "大专", "major": "中医康复技术", "years_of_experience": "8年",
        "expected_position": "高级理疗推拿师", "expected_city": "合肥",
        "expected_salary_min": 8000, "expected_salary_max": 15000,
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "core_skills": ["推拿", "按摩", "点穴", "经络疏通", "足底反射"],
        "skill_tags": ["高级康复理疗师", "拔罐", "艾灸", "刮痧"],
        "project_experience": [
            {
                "name": "天御足道旗舰店调理闭环", "technology": "极客防疲劳反射调理法",
                "description": "负责VIP客户重度腰肌劳损手法理疗，单月最高上钟超 220 钟，老客户回头率 85%。"
            }
        ]
    },
    {
        "resume_id": "903",
        "name": "李保洁", "highest_education": "初中", "major": "通用工科", "years_of_experience": "5年",
        "expected_position": "园区商业清洁工", "expected_city": "合肥",
        "expected_salary_min": 3500, "expected_salary_max": 4500,
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "core_skills": ["清扫", "除尘", "保洁药剂", "全面消杀"],
        "skill_tags": ["洗地机", "地毯抽洗机", "大理石结晶"],
        "project_experience": [
            {
                "name": "万科高新写字楼除油污保洁项目", "technology": "工业保洁药剂配比机制",
                "description": "主导地下车库重度除油污优化，对多租户企业生活垃圾进行精细化解构分类与秒级转运。"
            }
        ]
    },
    {
        "resume_id": "904",
        "name": "王客服", "highest_education": "大专", "major": "电子商务", "years_of_experience": "2年",
        "expected_position": "全栈在线客服专员", "expected_city": "合肥",
        "expected_salary_min": 5000, "expected_salary_max": 8000,
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "core_skills": ["普通话", "打字手速", "客情安抚", "工单编写"],
        "skill_tags": ["天猫客服", "京东售后", "流量削峰", "无感知回复"],
        "project_experience": [
            {
                "name": "三只松鼠售后钻石信道投诉突围", "technology": "情绪心理学话术重组",
                "description": "负责双十一期间高并发大单客诉接待，独立编写高质量无歧义业务工单流转至仓储部排队消费。"
            }
        ]
    },
    {
        "resume_id": "905",
        "name": "赵大单", "highest_education": "本科", "major": "市场营销", "years_of_experience": "3年",
        "expected_position": "高级大客户销售经理", "expected_city": "合肥",
        "expected_salary_min": 8000, "expected_salary_max": 12000,
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "core_skills": ["大客户发掘", "商务谈判", "招投标流转", "政企公关"],
        "skill_tags": ["关系网络织网", "项目运作", "决策链分析", "坏账熔断"],
        "project_experience": [
            {
                "name": "中联重科安徽重型机械招投标项目", "technology": "千万级订单控盘系统",
                "description": "负责打入政府建投核心决策链。单年度开单流水超 2500万元，成功回笼百万级账款资金流水。"
            }
        ]
    },
    {
        "resume_id": "906",
        "name": "孙主播", "highest_education": "大专", "major": "播音与主持", "years_of_experience": "2年",
        "expected_position": "带货网络主播", "expected_city": "合肥",
        "expected_salary_min": 10000, "expected_salary_max": 20000,
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "core_skills": ["出镜控盘", "逼单话术", "逐字脚本", "镜头留存"],
        "skill_tags": ["科技数码带货", "智能硬件", "节奏解构", "多维互动"],
        "project_experience": [
            {
                "name": "极客传媒智能硬件破限爆单直播", "technology": "弹幕削峰逼单引擎",
                "description": "负责智能卡片、人体工学椅直播带货，将国产达梦数据库、大模型RAG等参数通俗化拆解卖点。"
            }
        ]
    },
    {
        "resume_id": "907",
        "name": "周芯片", "highest_education": "本科", "major": "自动化", "years_of_experience": "2年",
        "expected_position": "嵌入式C语言开发工程师", "expected_city": "合肥",
        "expected_salary_min": 16000, "expected_salary_max": 24000,
        "job_family_bucket": "HARDWARE", "industry_bucket": "ELECTRONIC",
        "core_skills": ["C++", "C语言", "多线程并发中断", "微秒时序控制"],
        "skill_tags": ["STM32", "FreeRTOS", "互斥锁", "电路设计", "EMC"],
        "project_experience": [
            {
                "name": "阳光电源功率变流器驱动烧录项目", "technology": "C/C++底层寄存器指令集",
                "description": "负责大功率储能逆变器底层控制板的中断调优，切断物理链路熔断死锁与电磁干扰。"
            }
        ]
    },
    {
        "resume_id": "908",
        "name": "吴算法", "highest_education": "硕士", "major": "人工智能", "years_of_experience": "2年",
        "expected_position": "大模型文本切片算法工程师", "expected_city": "合肥",
        "expected_salary_min": 18000, "expected_salary_max": 30000,
        "job_family_bucket": "BACKEND", "industry_bucket": "IT_SOFTWARE",
        "core_skills": ["Python", "大模型全栈开发", "RAG架构", "向量空间余弦相似度"],
        "skill_tags": ["Ollama", "DeepSeek", "文本切片(Chunking)", "Embedding算法"],
        "project_experience": [
            {
                "name": "科大讯飞医疗电子病历结构化脱水检索系统", "technology": "text-embedding-v4 高维语义滑窗",
                "description": "主导百万级复杂大字段 CLOB 文本在 Linux 算子下的量化推理加速与智能高维匹配度算分。"
            }
        ]
    },
    {
        "resume_id": "909",
        "name": "郑数据", "highest_education": "本科", "major": "计算机科学", "years_of_experience": "4年",
        "expected_position": "信创达梦数据库 DBA 工程师", "expected_city": "合肥",
        "expected_salary_min": 14000, "expected_salary_max": 22000,
        "job_family_bucket": "BACKEND", "industry_bucket": "IT_SOFTWARE",
        "core_skills": ["达梦数据库", "SQL优化", "执行计划", "复杂多表关联", "锁机制"],
        "skill_tags": ["DM8", "信创适配", "联合索引", "全文检索索引", "死锁抢修"],
        "project_experience": [
            {
                "name": "安徽省政务服务中心达梦主库降延调优项目", "technology": "DMCP 高级管理指令集",
                "description": "主导千万级多租户政务流水数据分层处理，将慢 SQL 查询耗时从 12秒 一枪压缩至 40毫秒以内。"
            }
        ]
    },
    {
        "resume_id": "910",
        "name": "陈前端", "highest_education": "本科", "major": "软件工程", "years_of_experience": "3年",
        "expected_position": "高级前端开发工程师", "expected_city": "合肥",
        "expected_salary_min": 11000, "expected_salary_max": 17000,
        "job_family_bucket": "FRONTEND", "industry_bucket": "IT_SOFTWARE",
        "core_skills": ["Vue3", "Vue.js", "Pinia", "Axios深度封装", "响应式原理"],
        "skill_tags": ["Element Plus", "样式魔改", "组件二次封装", "ref组件唤醒", "SSE长连接"],
        "project_experience": [
            {
                "name": "科大国创高频高并发数据流大屏渲染项目", "technology": "EventSource 局部无感知刷新看板",
                "description": "利用组件 ref 动态挂载嵌套弹窗完成跨组件流变通信，攻克 Loading 卡片动态进化逻辑。"
            }
        ]
    }
]