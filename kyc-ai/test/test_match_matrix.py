# -*- coding: utf-8 -*-
import json
import math
import re
from difflib import SequenceMatcher
from typing import Any, List, Dict, Tuple, Set

# ==================== 1. 核心：技术别名、权重与语义组配置中心 ====================
RELATION_WEIGHTS = {"exact": 1.0, "alias": 0.95, "same_cluster": 0.85, "fuzzy": 0.75, "partial": 0.55, "missing": 0.0}
IMPORTANCE_WEIGHTS = {"must": 1.3, "required": 1.0, "important": 0.85, "preferred": 0.45}
SOURCE_CONFIDENCE = {"skill_evidence": 1.15, "project_experience": 1.10, "work_experience": 1.05,
                     "certifications": 1.00, "core_skills": 0.90, "skill_tags": 0.70}
LEVEL_WEIGHTS = {"精通": 1.20, "熟练": 1.12, "熟悉": 1.00, "了解": 0.82}
GENERIC_SKILL_WORDS = {"能力", "经验", "项目", "系统", "平台", "业务", "方案", "开发", "管理"}

# 🌟 重新焊死别名表，彻底击碎 NameError!
SKILL_ALIASES = {
    "vuejs": "vue", "vue2": "vue", "vue3": "vue", "reactjs": "react", "springboot": "springboot",
    "js": "javascript", "javascript": "javascript", "ts": "typescript", "typescript": "typescript",
    "mysql": "mysql", "达梦": "达梦数据库", "dm8": "达梦数据库", "达梦数据库": "达梦数据库",
    "推拿": "推拿", "按摩": "按摩", "保洁药剂": "去污药剂", "清扫": "清扫", "打字手速": "打字手速",
    "出镜控盘": "出镜控盘", "大模型全栈开发": "大模型", "大模型": "大模型", "c语言": "c语言", "c++": "c++"
}

SEMANTIC_SKILL_GROUPS = [
    {"Java", "Spring Boot", "MyBatis-Plus", "后端开发"},
    {"Vue", "vue", "Vue3", "Vue.js", "Element Plus", "Axios深度封装"},
    {"MySQL", "mysql", "达梦数据库", "SQL优化"}
]

# ==================== 2. 10大行业职位集 (生产级详细文档) ====================
test_jobs_data = [
    {
        "id": 101,
        "job_title": "Java全栈开发工程师（AI应用方向）",
        "company_name": "极客互娱（合肥）科技有限公司",
        "city": "合肥", "salary_min": 12000, "salary_max": 18000,
        "education_required": "大专", "experience_required": "1年",
        "job_family_bucket": "BACKEND", "industry_bucket": "IT_SOFTWARE",
        "required_skills": ["Java", "Spring Boot", "MySQL", "Vue3"],
        "preferred_skills": ["RabbitMQ", "达梦数据库", "Ollama", "DeepSeek"],
        "responsibilities": "负责企业级 B/C 双端服务平台的核心业务开发，利用 RabbitMQ 进行系统级解构，实现简历文本智能化脱水提取与算分。"
    },
    {
        "id": 102,
        "job_title": "Java全栈开发工程师（无人机IoT调度方向）",
        "company_name": "低空智控（合肥）航天科技有限公司",
        "city": "合肥", "salary_min": 14000, "salary_max": 22000,
        "education_required": "大专", "experience_required": "1年",
        "job_family_bucket": "BACKEND", "industry_bucket": "IT_SOFTWARE",
        "required_skills": ["Java", "Spring Boot", "MySQL", "Vue3"],
        "preferred_skills": ["RabbitMQ", "达梦数据库", "ECharts", "Ollama"],
        "responsibilities": "负责公司低空无人机运行轨迹规划单体服务开发，利用 RabbitMQ 构建百万级遥测数据排队消费引擎。"
    },
    {
        "id": 103,
        "job_title": "Java高级全栈开发工程师（信创司法AI与合同审计方向）",
        "company_name": "大国法眼（安徽）数字法律科技有限公司",
        "city": "合肥", "salary_min": 13000, "salary_max": 20000,
        "education_required": "大专", "experience_required": "1年",
        "job_family_bucket": "BACKEND", "industry_bucket": "IT_SOFTWARE",
        "required_skills": ["Java", "Spring Boot", "MySQL", "Vue3"],
        "preferred_skills": ["法律职业资格", "合同评审", "达梦数据库", "RabbitMQ"],
        "responsibilities": "负责数字法院合同评审全栈闭环开发，基于 RAG 架构进行高维法条向量配对算分与智能合规管理。"
    },
    {
        "id": 104,
        "job_title": "Java全栈开发工程师（智慧财税与智能审计方向）",
        "company_name": "浪潮精算（安徽）财税系统有限公司",
        "city": "合肥", "salary_min": 12000, "salary_max": 18000,
        "education_required": "大专", "experience_required": "1年",
        "job_family_bucket": "BACKEND", "industry_bucket": "IT_SOFTWARE",
        "required_skills": ["Java", "Spring Boot", "MySQL", "Vue3"],
        "preferred_skills": ["财务系统", "应付账款", "付款作业", "达梦数据库"],
        "responsibilities": "负责企业级智慧财税 B 端服务全栈式研发，实现百万级进销项发票与收付款凭证的秒级排队消费合规校验。"
    },
    {
        "id": 105,
        "job_title": "Java全栈开发工程师（储能EMS物联网监控方向）",
        "company_name": "绿能驭风（安徽）新能源储能科技有限公司",
        "city": "合肥", "salary_min": 16000, "salary_max": 24000,
        "education_required": "本科", "experience_required": "2年",
        "job_family_bucket": "BACKEND", "industry_bucket": "IT_SOFTWARE",
        "required_skills": ["Java", "Spring Boot", "MyBatis-Plus", "Vue3"],
        "preferred_skills": ["RabbitMQ", "达梦数据库", "电力电子", "逆变器"],
        "responsibilities": "负责光伏逆变器状态监控核心单体服务的核心代码编写，配合 Vue3 撑起跨端能效平衡交互。"
    },
    {
        "id": 106,
        "job_title": "Java全栈开发工程师（古建筑Web3D孪生与数字博物馆方向）",
        "company_name": "筑梦三维（安徽）古建数字雕琢科技有限公司",
        "city": "合肥", "salary_min": 14000, "salary_max": 20000,
        "education_required": "大专", "experience_required": "1年",
        "job_family_bucket": "FRONTEND", "industry_bucket": "IT_SOFTWARE",
        "required_skills": ["Spring Boot", "Vue3", "Three.js", "WebGL"],
        "preferred_skills": ["达梦数据库", "RabbitMQ", "SSE", "EventSource"],
        "responsibilities": "负责后端业务逻辑及前端古代中国榫卯结构的线上拆解展示，精通 Three.js / WebGL 的交互式渲染与模型打光。"
    },
    {
        "id": 107,
        "job_title": "高级理疗按摩师（中医推拿与足底反射方向）",
        "company_name": "足道乾坤（合肥）健康管理有限公司",
        "city": "合肥", "salary_min": 8000, "salary_max": 15000,
        "education_required": "不限", "experience_required": "1年",
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "required_skills": ["推拿", "按摩", "点穴", "经络疏通"],
        "preferred_skills": ["康复理疗师", "足底反射", "拔罐", "养生茶饮"],
        "responsibilities": "负责为到店客户提供高标准的中医推拿、足底反射区调理及精油 SPA 服务，解答健康疑问维护老客户续卡。"
    },
    {
        "id": 108,
        "job_title": "园区商业清洁工（重度保洁与设备洗刷方向）",
        "company_name": "洁净万家（安徽）物业服务有限公司",
        "city": "合肥", "salary_min": 3500, "salary_max": 4500,
        "education_required": "不限", "experience_required": "不限",
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "required_skills": ["清扫", "除尘", "消消杀", "去污药剂"],
        "preferred_skills": ["洗地机", "吸水机", "地毯清洗", "晶面翻新"],
        "responsibilities": "负责产业园写字楼大厅、公共卫生间日常清扫，熟练操作重型洗地机设备对广场进行大数据量重度除油污保洁优化。"
    },
    {
        "id": 109,
        "job_title": "全栈在线客服专员（B/C双端售后投诉与工单流转方向）",
        "company_name": "极速先锋（合肥）呼叫中心科技有限公司",
        "city": "合肥", "salary_min": 5000, "salary_max": 8000,
        "education_required": "大专", "experience_required": "1年",
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "required_skills": ["普通话", "打字手速", "客情沟通", "工单编写"],
        "preferred_skills": ["电商售后", "流程跟踪", "退退款拦截", "满意度回访"],
        "responsibilities": "负责平台线上客户接待，处理高并发客户投诉，编写高质量无歧义流转工单交由技术部排队消费。"
    },
    {
        "id": 110,
        "job_title": "带货网络主播（全栈科技数码与智能硬件带货方向）",
        "company_name": "霓虹极客（安徽）文化传媒有限公司",
        "city": "合肥", "salary_min": 10000, "salary_max": 20000,
        "job_family_bucket": "OTHER", "industry_bucket": "OTHER",
        "education_required": "大专", "experience_required": "1年",
        "required_skills": ["出镜控盘", "极客风范", "带货脚本", "逼单话术"],
        "preferred_skills": ["科技数码", "智能硬件", "弹幕削峰", "镜头感"],
        "responsibilities": "负责数码硬件、IT软件硬件等极客产品的出镜带货直播，通俗化拆解技术指标，配合中控达成爆单。"
    }
]

# ==================== 3. 10大硬核对照简历集 ====================
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


# ==================== 4. 脱机环境清洗与算分工具函数群 ====================
def is_missing(value: Any) -> bool:
    if value is None: return True
    if isinstance(value, str): return value.strip().lower() in {"", "未提及", "暂无", "无", "不详", "未知", "null",
                                                                "none"}
    return False


def to_text(value: Any) -> str:
    if value is None: return ""
    return str(value).strip()


def to_list(value: Any) -> list:
    if not value: return []
    if isinstance(value, list): return value
    return [value]


def normalize_bucket(value: Any) -> str:
    text = str(value or "").strip()
    return "" if not text or text == "OTHER" or is_missing(text) else text


def normalize_skill_key(skill: str) -> str:
    text = str(skill or "").strip().lower().replace(" ", "")
    return SKILL_ALIASES.get(text, text)


def same_semantic_group(left_key: str, right_key: str) -> bool:
    for group in SEMANTIC_SKILL_GROUPS:
        normalized = {normalize_skill_key(item) for item in group}
        if left_key in normalized and right_key in normalized: return True
    return False


def skill_relation(candidate_key: str, requirement_key: str) -> tuple:
    if not candidate_key or not requirement_key: return 0.0, "missing"
    if candidate_key == requirement_key: return RELATION_WEIGHTS["exact"], "exact"
    if candidate_key in GENERIC_SKILL_WORDS or requirement_key in GENERIC_SKILL_WORDS: return 0.0, "missing"
    if same_semantic_group(candidate_key, requirement_key): return RELATION_WEIGHTS["same_cluster"], "same_cluster"
    ratio = SequenceMatcher(None, candidate_key, requirement_key).ratio()
    if ratio >= 0.75: return RELATION_WEIGHTS["fuzzy"], "fuzzy"
    return 0.0, "missing"


def clamp(value: float, min_value: float = 0.0, max_value: float = 100.0) -> float:
    return min(max(value, min_value), max_value)


# ==================== 5. 核心：高维虚拟拓扑打分回归算法 ====================
def score_pair_offline(resume: dict, job: dict) -> float:
    required_skills = to_list(job.get("required_skills", []))
    preferred_skills = to_list(job.get("preferred_skills", []))
    all_requirements = required_skills + preferred_skills

    if not all_requirements: return 50.0

    candidate_skills = to_list(resume.get("core_skills", [])) + to_list(resume.get("skill_tags", []))
    for proj in to_list(resume.get("project_experience", [])):
        if isinstance(proj, dict):
            candidate_skills += to_list(proj.get("technology", ""))

    candidate_keys = {normalize_skill_key(s) for s in candidate_skills if s}

    # A. 技术栈覆盖率计算
    matched_weight = 0.0
    total_weight = len(all_requirements) * 1.0

    for req in all_requirements:
        req_key = normalize_skill_key(req)
        best_rel = 0.0
        for cand_key in candidate_keys:
            rel_w, _ = skill_relation(cand_key, req_key)
            if rel_w > best_rel: best_rel = rel_w
        matched_weight += best_rel * 1.0

    hard_skill_score = 35.0 * (matched_weight / total_weight if total_weight > 0 else 1.0)

    # B. 过滤器打分
    family_score = 25.0 if normalize_bucket(resume.get("job_family_bucket")) == normalize_bucket(
        job.get("job_family_bucket")) else 5.0
    city_score = 20.0 if str(resume.get("expected_city") or "") in str(job.get("city") or "") else 6.0

    salary_score = 20.0
    if resume.get("expected_salary_min") and job.get("salary_min"):
        if job.get("salary_max") < resume.get("expected_salary_min"): salary_score = 4.0

    # C. 综合加权合并
    employer_fit = hard_skill_score + 25.0
    candidate_fit = family_score + city_score + salary_score + 15.0

    final_score = employer_fit * 0.6 + candidate_fit * 0.4
    return round(min(max(final_score, 0.0), 100.0), 1)


# ==================== 6. 矩阵点火：全库 100 次交叉撞击 ====================
def run_full_matrix_bombing():
    print("=" * 75)
    print("🚀 [独立测试宇宙] KYC 人岗全矩阵交叉打分大轰炸机点火成功！")
    print(f"📊 加载测试岗位: {len(test_jobs_data)} 个 | 加载测试简历: {len(test_resumes_data)} 封")
    print(f"⚡ 即将触发 {len(test_jobs_data) * len(test_resumes_data)} 次高维引力波交叉角逐计算...")
    print("=" * 75 + "\n")

    for job in test_jobs_data:
        print(f"💼 【测评目标岗位】: ID {job['id']} | {job['job_title']} ({job['company_name']})")
        print("-" * 75)

        pair_scores = []
        for resume in test_resumes_data:
            score = score_pair_offline(resume, job)
            pair_scores.append({
                "name": resume["name"],
                "resume_id": resume["resume_id"],
                "score": score,
                "target_position": resume["expected_position"]
            })

        pair_scores.sort(key=lambda x: x["score"], reverse=True)

        for rank, item in enumerate(pair_scores, start=1):
            recommend_flag = "🔥 [HIGH 强强推荐]" if item["score"] >= 80 else "💤 [降权不推荐]"
            print(
                f"  🎖️ 名次 {rank} ➔ 得分: {item['score']}分 | {recommend_flag} 候选人: {item['name']} (ID: {item['resume_id']}) ➔ 意向: {item['target_position']}")
        print("\n" + "*" * 75 + "\n")


if __name__ == "__main__":
    run_full_matrix_bombing()