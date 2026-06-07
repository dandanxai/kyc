import hashlib
import json
import math
import os
import re
from datetime import datetime
from difflib import SequenceMatcher
from pathlib import Path
from typing import Any

import requests
from dotenv import load_dotenv
from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel, Field
import uvicorn


env_path = Path(__file__).resolve().parent / ".env"
load_dotenv(dotenv_path=env_path)

DASHSCOPE_BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1"
DASHSCOPE_API_KEY = os.getenv("DASHSCOPE_API_KEY", "")
PORT = int(os.getenv("MATCH_PORT", "8003"))
MODEL_TIMEOUT_SECONDS = 60
EMBEDDING_TIMEOUT_SECONDS = 30
EMBEDDING_MODEL = "text-embedding-v4"
EMBEDDING_DIMENSIONS = 1024
MAX_LLM_TOP_K = 20
SUITABLE_SCORE_THRESHOLD = 65.0

MISSING_VALUES = {"", "未提及", "暂无", "无", "不详", "未知", "null", "none"}
INVALID_LLM_PHRASES = {
    "一句话总结匹配结论",
    "主要风险点",
    "改进建议",
    "推荐或不推荐的关键依据",
}
GENERIC_LLM_FRAGMENTS = {
    "进一步沟通具体项目细节",
    "职业发展路径",
    "确认是否接受",
    "匹配度高，符合岗位要求",
}

EMPLOYER_WEIGHTS = {
    "hard_skill_coverage": 35.0,
    "evidence_path": 20.0,
    "semantic_similarity": 12.0,
    "project_responsibility": 10.0,
    "experience": 8.0,
    "education_major": 7.0,
    "preferred_soft": 5.0,
    "data_completeness": 3.0,
}
CANDIDATE_WEIGHTS = {
    "job_family": 25.0,
    "city": 20.0,
    "salary": 20.0,
    "industry": 15.0,
    "development_similarity": 10.0,
    "benefits_employment": 5.0,
    "data_completeness": 5.0,
}
RESUME_TO_JOBS_BLEND = {"employer": 0.6, "candidate": 0.4}
JOB_TO_RESUMES_BLEND = {"employer": 0.8, "candidate": 0.2}

RELATION_WEIGHTS = {
    "exact": 1.0,
    "alias": 0.95,
    "same_cluster": 0.85,
    "fuzzy": 0.75,
    "partial": 0.55,
    "missing": 0.0,
}
IMPORTANCE_WEIGHTS = {
    "must": 1.3,
    "required": 1.0,
    "important": 0.85,
    "preferred": 0.45,
}
SOURCE_CONFIDENCE = {
    "skill_evidence": 1.15,
    "project_experience": 1.10,
    "work_experience": 1.05,
    "certifications": 1.00,
    "core_skills": 0.90,
    "skill_tags": 0.70,
}
LEVEL_WEIGHTS = {
    "精通": 1.20,
    "熟练": 1.12,
    "熟悉": 1.00,
    "了解": 0.82,
}
EDUCATION_RANKS = {
    "不限": 0,
    "高中": 1,
    "中专": 1,
    "大专": 2,
    "专科": 2,
    "本科": 3,
    "硕士": 4,
    "研究生": 4,
    "博士": 5,
}
SKILL_ALIASES = {
    "vuejs": "vue",
    "vue2": "vue",
    "vue3": "vue",
    "vue": "vue",
    "reactjs": "react",
    "react": "react",
    "springboot": "springboot",
    "springcloud": "springcloud",
    "javascript": "javascript",
    "js": "javascript",
    "typescript": "typescript",
    "ts": "typescript",
    "nodejs": "nodejs",
    "node": "nodejs",
    "mysql": "mysql",
    "mysql数据库": "mysql",
    "redis": "redis",
    "linux": "linux",
    "docker": "docker",
    "kubernetes": "kubernetes",
    "k8s": "kubernetes",
    "达梦": "达梦数据库",
    "达梦数据库": "达梦数据库",
    "dm": "达梦数据库",
    "dm8": "达梦数据库",
    "dameng": "达梦数据库",
    "postgresql": "postgresql",
    "postgres": "postgresql",
    "mongodb": "mongodb",
    "mongo": "mongodb",
    "fastapi": "fastapi",
    "flask": "flask",
    "django": "django",
    "mybatis": "mybatis",
    "mybatisplus": "mybatisplus",
    "ollama": "ollama",
    "deepseek": "deepseek",
    "rag": "rag",
    "llm": "大模型",
    "大模型": "大模型",
    "大模型应用": "大模型",
    "llm应用": "大模型",
    "python": "python",
    "java": "java",
    "golang": "go",
    "go": "go",
    "c++": "c++",
    "c#": "c#",
    "法律职业资格证书": "法律职业资格",
    "法律职业资格": "法律职业资格",
    "法考": "法律职业资格",
    "司法考试": "法律职业资格",
    "合同审查": "合同评审",
    "合同审核": "合同评审",
    "合规": "合规管理",
    "财务系统": "财务系统",
    "财务软件": "财务系统",
    "应付": "应付账款",
    "应付会计": "应付账款",
    "收付款": "付款作业",
    "支付结算": "付款作业",
    "ai系统架构": "AI系统架构",
    "ai解决方案架构": "AI系统架构",
    "ai架构": "AI系统架构",
    "解决方案架构": "AI系统架构",
    "模型推理": "推理优化",
    "大模型推理": "推理优化",
    "推理加速": "推理优化",
    "推理优化": "推理优化",
    "gpu": "GPU",
    "npu": "NPU",
    "poc": "POC测试",
    "poc测试": "POC测试",
    "售前方案": "方案宣讲",
    "方案设计": "方案宣讲",
    "ict": "ICT行业",
    "ict行业": "ICT行业",
    "ict解决方案": "ICT解决方案",
    "服务解决方案": "服务解决方案",
    "项目运作": "项目运作",
    "渠道管理": "伙伴管理",
    "渠道伙伴": "伙伴管理",
    "客户维护": "客户关系",
    "客户沟通": "客户沟通",
    "电力电子": "电力电子",
    "功率电子": "电力电子",
    "功率硬件": "功率硬件",
    "逆变器": "逆变器",
    "光伏逆变器": "逆变器",
    "emc": "EMC",
    "halt": "Halt",
}
SEMANTIC_SKILL_GROUPS = [
    {"Java", "Spring Boot", "Spring Cloud", "MyBatis", "后端开发", "微服务", "Dubbo"},
    {"Vue", "vue", "Vue.js", "ElementUI", "Axios", "前端开发"},
    {"SQL", "MySQL", "mysql", "数据库", "数据仓库"},
    {"数据分析", "BI", "数据看板", "指标分析", "统计分析", "数据质量"},
    {"大模型", "AI系统架构", "AI框架", "推理优化", "vLLM", "SGLang", "GPU", "NPU", "CUDA", "算子优化", "量化"},
    {"ICT行业", "ICT解决方案", "POC测试", "方案宣讲", "配置报价", "数通", "无线", "存储", "服务解决方案"},
    {"渠道销售", "伙伴管理", "市场推广", "客户关系", "商务谈判", "电商运营"},
    {"合同评审", "法律职业资格", "交易风险", "争议处理", "合规管理", "诉讼案件"},
    {"会计", "应付账款", "付款作业", "财务管理", "财务系统", "SAP", "Excel", "银行结算"},
    {"电力电子", "功率硬件", "逆变器", "电路设计", "EMC", "热设计", "Halt", "储能变流器"},
    {"项目管理", "PMP", "ITIL"},
    {"政务云", "云计算", "分布式系统", "系统架构师", "微服务"},
]
GENERIC_SKILL_WORDS = {"能力", "经验", "项目", "系统", "平台", "业务", "方案", "开发", "管理"}

embedding_cache: dict[str, list[float]] = {}

app = FastAPI(
    title="KYC Talent-Job Match Service V2",
    description="基于虚拟能力图谱、向量语义和可解释多维算法的人岗双向匹配服务",
    version="2.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class ResumeToJobsRequest(BaseModel):
    resume: dict[str, Any] = Field(..., description="url_api 返回的简历 data 对象")
    jobs: list[dict[str, Any]] = Field(..., description="url_api 返回的岗位 data 对象数组")
    enableLLM: bool = Field(True, description="是否启用大模型解释复核")
    enableEmbedding: bool = Field(True, description="是否启用百炼 text-embedding-v4 语义向量")
    includeTrace: bool = Field(True, description="是否返回 graph_summary 和 evidence_paths")
    llmTopK: int = Field(20, ge=0, description="规则排序后交给大模型复核的数量，最多 20")


class JobToResumesRequest(BaseModel):
    job: dict[str, Any] = Field(..., description="url_api 返回的岗位 data 对象")
    resumes: list[dict[str, Any]] = Field(..., description="url_api 返回的简历 data 对象数组")
    enableLLM: bool = Field(True, description="是否启用大模型解释复核")
    enableEmbedding: bool = Field(True, description="是否启用百炼 text-embedding-v4 语义向量")
    includeTrace: bool = Field(True, description="是否返回 graph_summary 和 evidence_paths")
    llmTopK: int = Field(20, ge=0, description="规则排序后交给大模型复核的数量，最多 20")


def api_response(code: int, success: bool, message: str, data: Any = None) -> JSONResponse:
    return JSONResponse(
        status_code=code,
        content={
            "code": code,
            "success": success,
            "message": message,
            "data": data if data is not None else {},
        },
    )


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    return api_response(422, False, "请求参数格式错误", {"errors": exc.errors()})


def is_missing(value: Any) -> bool:
    if value is None:
        return True
    if isinstance(value, str):
        return value.strip().lower() in MISSING_VALUES
    return False


def to_text(value: Any, default: str = "未提及") -> str:
    if is_missing(value):
        return default
    if isinstance(value, str):
        return value.strip() or default
    if isinstance(value, (int, float)):
        return str(value)
    if isinstance(value, (list, tuple, set)):
        parts = [to_text(item, "") for item in value]
        return "；".join([part for part in parts if part]) or default
    if isinstance(value, dict):
        return json.dumps(value, ensure_ascii=False)
    return str(value).strip() or default


def display_text(value: Any, default: str = "未提及") -> str:
    if is_missing(value):
        return default
    if isinstance(value, (list, tuple, set)):
        parts = [to_text(item, "") for item in value]
        return "、".join([part for part in parts if part]) or default
    return to_text(value, default)


def to_list(value: Any) -> list[Any]:
    if is_missing(value):
        return []
    if isinstance(value, list):
        return [item for item in value if not is_missing(item)]
    if isinstance(value, tuple):
        return [item for item in value if not is_missing(item)]
    if isinstance(value, set):
        return [item for item in value if not is_missing(item)]
    if isinstance(value, str):
        parts = re.split(r"[、,，;；\n\r|/]+", value)
        return [part.strip() for part in parts if part.strip() and not is_missing(part)]
    return [value]


def first_present(*values: Any, default: str = "未提及") -> str:
    for value in values:
        if not is_missing(value):
            return to_text(value)
    return default


def merge_unique(values: list[Any]) -> list[str]:
    result: list[str] = []
    seen: set[str] = set()
    for value in values:
        text = to_text(value, "")
        if not text or text in seen:
            continue
        seen.add(text)
        result.append(text)
    return result


def dedupe_keep_order(items: list[Any]) -> list[Any]:
    seen: set[str] = set()
    result = []
    for item in items:
        key = json.dumps(item, ensure_ascii=False, sort_keys=True) if isinstance(item, dict) else to_text(item, "")
        if not key or key in seen:
            continue
        seen.add(key)
        result.append(item)
    return result


def clamp(value: float, min_value: float = 0.0, max_value: float = 100.0) -> float:
    return min(max(value, min_value), max_value)


def number_or_none(value: Any, salary: bool = False) -> float | None:
    if is_missing(value) or isinstance(value, bool):
        return None
    if isinstance(value, (int, float)):
        if salary and value <= 0:
            return None
        return float(value)
    text = to_text(value, "").lower()
    match = re.search(r"\d+(?:\.\d+)?", text)
    if not match:
        return None
    number = float(match.group())
    if salary and number <= 0:
        return None
    if salary:
        if "万" in text or "w" in text:
            number *= 10000
        elif "千" in text or "k" in text:
            number *= 1000
    return number


def normalize_skill_key(skill: Any) -> str:
    text = to_text(skill, "").lower()
    text = text.replace("（", "(").replace("）", ")")
    text = re.sub(r"\([^)]*\)", "", text)
    text = re.sub(r"[\s._\-]+", "", text)
    return SKILL_ALIASES.get(text, text)


def skill_name(item: Any) -> str:
    if isinstance(item, dict):
        return first_present(item.get("skill"), item.get("name"), item.get("label"), default="")
    return to_text(item, "")


def split_skill_text(value: Any) -> list[str]:
    text = to_text(value, "")
    if not text:
        return []
    parts = re.split(r"\s+\+\s+|[、,，;；\n\r|]+", text)
    return [part.strip() for part in parts if part.strip()]


def normalize_importance(value: Any, default: str = "required") -> str:
    text = to_text(value, "").lower()
    if any(word in text for word in ["must", "必须", "强制", "核心", "硬性"]):
        return "must"
    if any(word in text for word in ["preferred", "加分", "优先", "nice"]):
        return "preferred"
    if any(word in text for word in ["important", "重要", "熟练", "掌握"]):
        return "important"
    if any(word in text for word in ["required", "必备", "要求"]):
        return "required"
    return default


def level_weight(level: Any) -> float:
    level_text = to_text(level, "")
    for keyword, weight in LEVEL_WEIGHTS.items():
        if keyword in level_text:
            return weight
    return 1.0


def clean_ai_content(ai_content: str) -> str:
    ai_content = ai_content.strip()
    if ai_content.startswith("```json"):
        ai_content = ai_content.replace("```json", "", 1)
    if ai_content.startswith("```"):
        ai_content = ai_content.replace("```", "", 1)
    if ai_content.endswith("```"):
        ai_content = ai_content[:-3]
    return ai_content.strip()


def parse_llm_json(ai_content: str) -> dict[str, Any]:
    cleaned = clean_ai_content(ai_content)
    try:
        parsed = json.loads(cleaned)
    except json.JSONDecodeError:
        start = cleaned.find("{")
        end = cleaned.rfind("}")
        if start == -1 or end == -1 or end <= start:
            raise
        parsed = json.loads(cleaned[start : end + 1])
    if not isinstance(parsed, dict):
        raise ValueError("大模型返回的 JSON 顶层不是对象")
    return parsed


def call_llm(prompt: str) -> dict[str, Any]:
    if not DASHSCOPE_API_KEY:
        raise RuntimeError("未配置 DASHSCOPE_API_KEY")

    headers = {
        "Authorization": f"Bearer {DASHSCOPE_API_KEY}",
        "Content-Type": "application/json",
    }
    payload = {
        "model": "qwen-turbo",
        "messages": [
            {"role": "system", "content": "你是人岗匹配解释专家，只能返回合法 JSON。"},
            {"role": "user", "content": prompt},
        ],
        "temperature": 0.2,
        "stream": False,
    }
    response = requests.post(
        f"{DASHSCOPE_BASE_URL}/chat/completions",
        headers=headers,
        json=payload,
        timeout=MODEL_TIMEOUT_SECONDS,
    )
    if response.status_code != 200:
        raise RuntimeError(f"阿里云百炼 API 响应异常，HTTP {response.status_code}: {response.text}")
    return parse_llm_json(response.json()["choices"][0]["message"]["content"])


def text_hash(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def call_embedding(text: str) -> tuple[list[float] | None, str]:
    if not DASHSCOPE_API_KEY:
        return None, "未配置 DASHSCOPE_API_KEY"
    compact_text = text.strip()[:6000]
    if not compact_text:
        return None, "文本为空"
    cache_key = text_hash(compact_text)
    if cache_key in embedding_cache:
        return embedding_cache[cache_key], ""

    headers = {
        "Authorization": f"Bearer {DASHSCOPE_API_KEY}",
        "Content-Type": "application/json",
    }
    payload = {
        "model": EMBEDDING_MODEL,
        "input": compact_text,
        "dimensions": EMBEDDING_DIMENSIONS,
        "encoding_format": "float",
    }
    try:
        response = requests.post(
            f"{DASHSCOPE_BASE_URL}/embeddings",
            headers=headers,
            json=payload,
            timeout=EMBEDDING_TIMEOUT_SECONDS,
        )
        if response.status_code != 200:
            return None, f"Embedding HTTP {response.status_code}: {response.text[:160]}"
        data = response.json()
        vector = data["data"][0]["embedding"]
        if not isinstance(vector, list) or not vector:
            return None, "Embedding 返回为空"
        embedding_cache[cache_key] = [float(item) for item in vector]
        return embedding_cache[cache_key], ""
    except Exception as exc:
        return None, f"Embedding 调用失败：{to_text(exc)[:160]}"


def cosine_similarity(left: list[float] | None, right: list[float] | None) -> float | None:
    if not left or not right or len(left) != len(right):
        return None
    dot = sum(a * b for a, b in zip(left, right))
    left_norm = math.sqrt(sum(a * a for a in left))
    right_norm = math.sqrt(sum(b * b for b in right))
    if left_norm == 0 or right_norm == 0:
        return None
    return max(0.0, min(1.0, dot / (left_norm * right_norm)))


def normalize_bucket(value: Any) -> str:
    text = to_text(value, "").strip()
    return "" if not text or text == "OTHER" or is_missing(text) else text


def flatten_text(value: Any) -> str:
    if is_missing(value):
        return ""
    if isinstance(value, dict):
        return " ".join(flatten_text(item) for item in value.values())
    if isinstance(value, (list, tuple, set)):
        return " ".join(flatten_text(item) for item in value)
    return to_text(value, "")


def build_resume_profile_text(resume: dict[str, Any]) -> str:
    parts = [
        resume.get("expected_position"),
        resume.get("profile_summary"),
        resume.get("core_skills"),
        resume.get("skill_tags"),
        resume.get("certifications"),
        resume.get("skill_evidence"),
        resume.get("work_experience"),
        resume.get("project_experience"),
        resume.get("achievements"),
        resume.get("self_evaluation"),
    ]
    return flatten_text(parts)


def build_job_profile_text(job: dict[str, Any]) -> str:
    parts = [
        job.get("job_title"),
        job.get("job_category"),
        job.get("job_summary"),
        job.get("responsibilities"),
        job.get("requirements"),
        job.get("preferred"),
        job.get("required_skills"),
        job.get("preferred_skills"),
        job.get("soft_skills"),
        job.get("keywords"),
        job.get("skill_requirements"),
    ]
    return flatten_text(parts)


def add_node(graph: dict[str, Any], node_id: str, node_type: str, label: str, properties: dict[str, Any] | None = None) -> str:
    if node_id not in graph["_node_index"]:
        node = {
            "id": node_id,
            "type": node_type,
            "label": label,
            "properties": properties or {},
        }
        graph["_node_index"][node_id] = node
        graph["nodes"].append(node)
    return node_id


def add_edge(
    graph: dict[str, Any],
    source: str,
    target: str,
    relation: str,
    weight: float = 1.0,
    properties: dict[str, Any] | None = None,
) -> None:
    graph["edges"].append(
        {
            "source": source,
            "target": target,
            "relation": relation,
            "weight": round(weight, 3),
            "properties": properties or {},
        }
    )


def empty_graph(graph_type: str) -> dict[str, Any]:
    return {"type": graph_type, "nodes": [], "edges": [], "_node_index": {}}


def source_node_type(source: str) -> str:
    if source == "project_experience":
        return "ProjectExperience"
    if source == "work_experience":
        return "WorkExperience"
    if source == "certifications":
        return "Certificate"
    return "Candidate"


def skill_node_id(prefix: str, skill_key: str) -> str:
    safe_key = re.sub(r"[^a-zA-Z0-9_\u4e00-\u9fa5]+", "_", skill_key)
    return f"{prefix}_skill_{safe_key}"


def add_candidate_skill(
    graph: dict[str, Any],
    profile: dict[str, dict[str, Any]],
    candidate_id: str,
    skill: Any,
    source: str,
    source_node_id: str,
    level: Any = None,
    evidence: Any = None,
) -> None:
    name = skill_name(skill)
    key = normalize_skill_key(name)
    if not key:
        return
    level_factor = level_weight(level)
    confidence = clamp(SOURCE_CONFIDENCE.get(source, 0.70) * level_factor, 0.35, 1.25)
    skill_id = skill_node_id("candidate", key)
    add_node(graph, skill_id, "Skill", SKILL_ALIASES.get(key, name), {"key": key})
    relation = "USES_SKILL" if source in {"project_experience", "work_experience"} else "HAS_SKILL"
    add_edge(
        graph,
        source_node_id,
        skill_id,
        relation,
        confidence,
        {"source": source, "level": to_text(level, ""), "evidence": to_text(evidence, "")},
    )

    entry = profile.setdefault(
        key,
        {
            "name": SKILL_ALIASES.get(key, name),
            "key": key,
            "confidence": 0.0,
            "sources": [],
            "evidence": [],
            "source_nodes": [],
        },
    )
    if confidence > entry["confidence"]:
        entry["confidence"] = confidence
        entry["name"] = SKILL_ALIASES.get(key, name)
    if source not in entry["sources"]:
        entry["sources"].append(source)
    if source_node_id not in entry["source_nodes"]:
        entry["source_nodes"].append(source_node_id)
    if not is_missing(evidence):
        entry["evidence"].append(to_text(evidence, ""))

    if source_node_id != candidate_id:
        add_edge(graph, candidate_id, source_node_id, "HAS_EVIDENCE", 1.0, {"source": source})


def build_candidate_graph(resume: dict[str, Any], resume_id: str) -> dict[str, Any]:
    graph = empty_graph("candidate")
    candidate_node = f"candidate_{resume_id}"
    add_node(graph, candidate_node, "Candidate", first_present(resume.get("name"), default=resume_id), {"resume_id": resume_id})
    profile: dict[str, dict[str, Any]] = {}

    for skill in to_list(resume.get("core_skills")):
        add_candidate_skill(graph, profile, candidate_node, skill, "core_skills", candidate_node)
    for skill in to_list(resume.get("skill_tags")):
        add_candidate_skill(graph, profile, candidate_node, skill, "skill_tags", candidate_node)
    for cert in to_list(resume.get("certifications")):
        cert_label = skill_name(cert)
        cert_node = f"{candidate_node}_cert_{len(graph['nodes'])}"
        add_node(graph, cert_node, "Certificate", cert_label, {"source": "certifications"})
        add_edge(graph, candidate_node, cert_node, "HAS_CERTIFICATE", 1.0)
        add_candidate_skill(graph, profile, candidate_node, cert, "certifications", cert_node, evidence=cert_label)

    for index, item in enumerate(to_list(resume.get("work_experience"))):
        if not isinstance(item, dict):
            continue
        node_id = f"{candidate_node}_work_{index}"
        add_node(
            graph,
            node_id,
            "WorkExperience",
            first_present(item.get("company"), item.get("position"), default=f"工作经历{index + 1}"),
            {"duration": item.get("duration"), "description": item.get("description")},
        )
        add_edge(graph, candidate_node, node_id, "HAS_WORK_EXPERIENCE", 1.0)
        for skill in to_list(item.get("skills_used")):
            add_candidate_skill(graph, profile, candidate_node, skill, "work_experience", node_id, evidence=item.get("description"))

    for index, item in enumerate(to_list(resume.get("project_experience"))):
        if not isinstance(item, dict):
            continue
        node_id = f"{candidate_node}_project_{index}"
        add_node(
            graph,
            node_id,
            "ProjectExperience",
            first_present(item.get("project_name"), item.get("name"), default=f"项目经历{index + 1}"),
            {"duration": item.get("duration"), "description": item.get("description"), "achievements": item.get("achievements")},
        )
        add_edge(graph, candidate_node, node_id, "HAS_PROJECT_EXPERIENCE", 1.0)
        project_skills = to_list(item.get("tech_stack")) + split_skill_text(item.get("technology"))
        for skill in project_skills:
            add_candidate_skill(graph, profile, candidate_node, skill, "project_experience", node_id, evidence=item.get("description"))

    for item in to_list(resume.get("skill_evidence")):
        if isinstance(item, dict):
            add_candidate_skill(
                graph,
                profile,
                candidate_node,
                skill_name(item),
                "skill_evidence",
                candidate_node,
                level=item.get("level"),
                evidence=item.get("evidence"),
            )
        else:
            add_candidate_skill(graph, profile, candidate_node, item, "skill_evidence", candidate_node)

    graph["skill_profile"] = profile
    graph["root"] = candidate_node
    return graph


def job_skill_requirement(name: Any, importance: str = "required", evidence: Any = None) -> dict[str, Any] | None:
    skill = skill_name(name)
    key = normalize_skill_key(skill)
    if not key:
        return None
    importance_key = normalize_importance(importance)
    return {
        "skill": skill,
        "key": key,
        "importance": importance_key,
        "importance_weight": IMPORTANCE_WEIGHTS.get(importance_key, 1.0),
        "evidence": to_text(evidence, ""),
    }


def dedupe_requirements(requirements: list[dict[str, Any]]) -> list[dict[str, Any]]:
    result: dict[str, dict[str, Any]] = {}
    for item in requirements:
        key = item["key"]
        existing = result.get(key)
        if existing is None or item["importance_weight"] > existing["importance_weight"]:
            result[key] = item
    return list(result.values())


def collect_job_requirements(job: dict[str, Any], preferred: bool) -> list[dict[str, Any]]:
    requirements: list[dict[str, Any]] = []
    field_name = "preferred_skills" if preferred else "required_skills"
    for skill in to_list(job.get(field_name)):
        entry = job_skill_requirement(skill, "preferred" if preferred else "required")
        if entry:
            requirements.append(entry)
    for item in to_list(job.get("skill_requirements")):
        if not isinstance(item, dict):
            continue
        importance = normalize_importance(item.get("importance"))
        is_preferred = importance == "preferred"
        if is_preferred == preferred:
            entry = job_skill_requirement(skill_name(item), importance, item.get("evidence"))
            if entry:
                requirements.append(entry)
    return dedupe_requirements(requirements)


def build_job_graph(job: dict[str, Any], job_id: str) -> dict[str, Any]:
    graph = empty_graph("job")
    job_node = f"job_{job_id}"
    add_node(graph, job_node, "Job", first_present(job.get("job_title"), default=job_id), {"job_id": job_id})
    required = collect_job_requirements(job, preferred=False)
    preferred = collect_job_requirements(job, preferred=True)

    for requirement in required + preferred:
        skill_id = skill_node_id("job", requirement["key"])
        add_node(graph, skill_id, "Skill", requirement["skill"], {"key": requirement["key"]})
        relation = "PREFERS_SKILL" if requirement["importance"] == "preferred" else "REQUIRES_SKILL"
        add_edge(
            graph,
            job_node,
            skill_id,
            relation,
            requirement["importance_weight"],
            {"importance": requirement["importance"], "evidence": requirement["evidence"]},
        )

    graph["required_requirements"] = required
    graph["preferred_requirements"] = preferred
    graph["root"] = job_node
    return graph


def same_semantic_group(left_key: str, right_key: str) -> bool:
    for group in SEMANTIC_SKILL_GROUPS:
        normalized = {normalize_skill_key(item) for item in group}
        if left_key in normalized and right_key in normalized:
            return True
    return False


def is_generic_skill_key(key: str) -> bool:
    return key in GENERIC_SKILL_WORDS or len(key) <= 1


def skill_relation(candidate_key: str, requirement_key: str) -> tuple[float, str]:
    if not candidate_key or not requirement_key:
        return 0.0, "missing"
    if candidate_key == requirement_key:
        return RELATION_WEIGHTS["exact"], "exact"
    if is_generic_skill_key(candidate_key) or is_generic_skill_key(requirement_key):
        return 0.0, "missing"
    shorter, longer = sorted([candidate_key, requirement_key], key=len)
    if len(shorter) >= 4 and shorter in longer and {shorter, longer} != {"java", "javascript"}:
        return RELATION_WEIGHTS["alias"], "alias"
    if same_semantic_group(candidate_key, requirement_key):
        return RELATION_WEIGHTS["same_cluster"], "same_cluster"
    ratio = SequenceMatcher(None, candidate_key, requirement_key).ratio()
    if ratio >= 0.88:
        return RELATION_WEIGHTS["fuzzy"], "fuzzy"
    if ratio >= 0.72:
        return RELATION_WEIGHTS["partial"], "partial"
    return 0.0, "missing"


def best_skill_match(requirement: dict[str, Any], candidate_profile: dict[str, dict[str, Any]]) -> dict[str, Any]:
    best = {
        "requirement": requirement,
        "candidate": None,
        "relation_weight": 0.0,
        "relation_type": "missing",
        "source_confidence": 0.0,
        "path_weight": 0.0,
    }
    for candidate in candidate_profile.values():
        relation_weight, relation_type = skill_relation(candidate["key"], requirement["key"])
        if relation_weight <= 0:
            continue
        source_confidence = clamp(float(candidate.get("confidence", 0.7)), 0.35, 1.25)
        decay = 0.85 if any(source in candidate.get("sources", []) for source in ["project_experience", "work_experience"]) else 1.0
        path_weight = requirement["importance_weight"] * relation_weight * source_confidence * decay
        if path_weight > best["path_weight"]:
            best = {
                "requirement": requirement,
                "candidate": candidate,
                "relation_weight": relation_weight,
                "relation_type": relation_type,
                "source_confidence": source_confidence,
                "decay": decay,
                "path_weight": path_weight,
            }
    return best


def build_evidence_path(match: dict[str, Any]) -> dict[str, Any] | None:
    candidate = match.get("candidate")
    requirement = match["requirement"]
    if not candidate:
        return {
            "path": f"岗位要求 -> {requirement['skill']} -> 候选人无明确证据",
            "weight": 0.0,
            "relation_type": "missing",
            "source_confidence": 0.0,
            "decay": 1.0,
            "reason": "岗位硬性技能在候选人结构化画像中未找到可用证据。",
        }
    source = "、".join(candidate.get("sources", [])[:3])
    path = (
        f"候选人 -> {source or '能力画像'} -> {candidate['name']} "
        f"-> {match['relation_type']} -> {requirement['skill']} -> 岗位要求"
    )
    return {
        "path": path,
        "weight": round(match["path_weight"], 3),
        "relation_type": match["relation_type"],
        "source_confidence": round(match["source_confidence"], 3),
        "decay": round(match.get("decay", 1.0), 3),
        "reason": f"{requirement['skill']} 由候选人的 {source or '能力画像'} 形成匹配证据。",
    }


def score_required_skills(candidate_graph: dict[str, Any], job_graph: dict[str, Any]) -> dict[str, Any]:
    required = job_graph["required_requirements"]
    candidate_profile = candidate_graph["skill_profile"]
    if not required:
        return {
            "coverage_score": 24.0,
            "evidence_score": 12.0,
            "matched": [],
            "partial": [],
            "missing": [],
            "paths": [],
            "uncertain": [],
            "reasons": ["岗位未明确硬性技能，技能维度按中性分处理。"],
        }

    total_importance = sum(item["importance_weight"] for item in required) or 1.0
    coverage_weight = 0.0
    evidence_weight = 0.0
    matched: list[str] = []
    partial: list[str] = []
    missing: list[str] = []
    paths: list[dict[str, Any]] = []
    uncertain: list[dict[str, Any]] = []

    for requirement in required:
        match = best_skill_match(requirement, candidate_profile)
        coverage_weight += requirement["importance_weight"] * match["relation_weight"]
        evidence_weight += min(requirement["importance_weight"], match["path_weight"])
        if match["relation_weight"] >= 0.85:
            matched.append(requirement["skill"])
        elif match["relation_weight"] >= 0.55:
            partial.append(requirement["skill"])
            if match.get("candidate"):
                uncertain.append(
                    {
                        "required_skill": requirement["skill"],
                        "candidate_skill": match["candidate"]["name"],
                        "relation_type": match["relation_type"],
                        "score": round(match["relation_weight"], 2),
                    }
                )
        else:
            missing.append(requirement["skill"])
        path = build_evidence_path(match)
        if path:
            paths.append(path)

    coverage_score = 35.0 * coverage_weight / total_importance
    evidence_score = 20.0 * evidence_weight / total_importance
    reasons = []
    if matched:
        reasons.append(f"硬性技能命中：{'、'.join(matched[:8])}。")
    if partial:
        reasons.append(f"硬性技能部分相关：{'、'.join(partial[:6])}。")
    if missing:
        reasons.append(f"缺少硬性技能：{'、'.join(missing[:8])}。")
    return {
        "coverage_score": round(clamp(coverage_score, 0, 35), 1),
        "evidence_score": round(clamp(evidence_score, 0, 20), 1),
        "matched": matched,
        "partial": partial,
        "missing": missing,
        "paths": sorted(paths, key=lambda item: item["weight"], reverse=True),
        "uncertain": uncertain,
        "reasons": reasons,
    }


def score_preferred_soft(candidate_graph: dict[str, Any], job_graph: dict[str, Any], job: dict[str, Any]) -> tuple[float, list[str], list[str]]:
    preferred = job_graph["preferred_requirements"]
    soft_skills = [job_skill_requirement(skill, "preferred") for skill in to_list(job.get("soft_skills"))]
    soft_requirements = [item for item in soft_skills if item]
    requirements = preferred + soft_requirements
    if not requirements:
        return 3.0, [], ["岗位加分技能/软技能较少，按中性分处理。"]

    candidate_profile = candidate_graph["skill_profile"]
    matched: list[str] = []
    total = len(requirements)
    for requirement in requirements:
        match = best_skill_match(requirement, candidate_profile)
        if match["relation_weight"] >= 0.55:
            matched.append(requirement["skill"])
    score = 5.0 * len(matched) / total
    reasons = [f"加分技能/软技能命中 {len(matched)}/{total}。"]
    return round(score, 1), matched, reasons


def normalize_component_scores(scores: dict[str, float], weights: dict[str, float], omitted: set[str]) -> tuple[float, dict[str, float], float]:
    active_total = sum(weight for key, weight in weights.items() if key not in omitted)
    full_total = sum(weights.values())
    raw = sum(scores.get(key, 0.0) for key in weights if key not in omitted)
    factor = full_total / active_total if active_total else 1.0
    normalized = clamp(raw * factor)
    return round(normalized, 1), {key: round(value, 1) for key, value in scores.items()}, round(factor, 3)


def build_salary_range(min_value: Any, max_value: Any) -> tuple[float, float] | None:
    min_number = number_or_none(min_value, salary=True)
    max_number = number_or_none(max_value, salary=True)
    if min_number is None and max_number is None:
        return None
    if min_number is None:
        min_number = max_number
    if max_number is None:
        max_number = min_number
    if min_number is None or max_number is None:
        return None
    if min_number > max_number:
        min_number, max_number = max_number, min_number
    return min_number, max_number


def normalize_city(value: Any) -> str:
    return to_text(value, "").strip().replace("市", "").replace("省", "")


def extract_cities(value: Any) -> list[str]:
    return [normalize_city(item) for item in to_list(value) if normalize_city(item)]


def city_matches(resume_city: str, job_city: str) -> bool:
    if not resume_city or not job_city:
        return False
    if any(word in resume_city for word in ["不限", "全国", "远程"]):
        return True
    if any(word in job_city for word in ["不限", "全国", "远程"]):
        return True
    return resume_city == job_city or resume_city in job_city or job_city in resume_city


def score_city(resume: dict[str, Any], job: dict[str, Any], max_score: float) -> tuple[float, list[str], list[str]]:
    job_city = normalize_city(job.get("city"))
    work_mode = to_text(job.get("work_mode"), "")
    resume_cities = dedupe_keep_order(extract_cities(resume.get("expected_city")) + extract_cities(resume.get("current_city")))
    if any(word in work_mode.lower() for word in ["远程", "remote", "线上"]):
        return max_score, [], ["岗位支持远程办公，城市维度视为匹配。"]
    if any(word in work_mode.lower() for word in ["混合", "hybrid"]):
        if not job_city or not resume_cities:
            return round(max_score * 0.8, 1), [], ["岗位为混合办公，城市信息不完整，按较高中性分处理。"]
        if any(city_matches(city, job_city) for city in resume_cities):
            return max_score, [], ["岗位为混合办公且城市匹配。"]
        return round(max_score * 0.6, 1), ["CITY_MISMATCH"], ["岗位为混合办公但城市不一致，轻度降权。"]
    if not job_city or not resume_cities:
        return round(max_score * 0.6, 1), [], ["城市信息不完整，按中性分处理。"]
    if any(city_matches(city, job_city) for city in resume_cities):
        return max_score, [], ["候选人城市意向与岗位城市匹配。"]
    return round(max_score * 0.3, 1), ["CITY_MISMATCH"], ["候选人城市意向与岗位城市不一致，已降权。"]


def score_salary(resume: dict[str, Any], job: dict[str, Any], max_score: float) -> tuple[float, list[str], list[str]]:
    expected_range = build_salary_range(resume.get("expected_salary_min"), resume.get("expected_salary_max"))
    job_range = build_salary_range(job.get("salary_min"), job.get("salary_max"))
    if expected_range is None or job_range is None:
        return round(max_score * 0.6, 1), [], ["薪资信息不完整，按中性分处理。"]
    expected_min, expected_max = expected_range
    job_min, job_max = job_range
    if job_min <= expected_min and job_max >= expected_max:
        return max_score, [], ["岗位薪资完整覆盖候选人期望区间。"]
    if job_min > expected_max:
        return max_score, [], ["岗位薪资高于候选人期望上限，视为匹配。"]
    overlap_min = max(expected_min, job_min)
    overlap_max = min(expected_max, job_max)
    if overlap_min <= overlap_max:
        return round(max_score * 0.8, 1), [], ["岗位薪资与候选人期望存在部分重叠。"]
    if job_max >= expected_min * 0.9:
        return round(max_score * 0.5, 1), ["SALARY_NOT_OVERLAP"], ["岗位薪资略低于候选人期望，轻度降权。"]
    return round(max_score * 0.3, 1), ["SALARY_NOT_OVERLAP"], ["岗位薪资明显低于候选人期望，已降权。"]


def education_rank(value: Any) -> int | None:
    text = to_text(value, "")
    if not text:
        return None
    for keyword, rank in EDUCATION_RANKS.items():
        if keyword in text:
            return rank
    return None


def score_education_major(resume: dict[str, Any], job: dict[str, Any], max_score: float) -> tuple[float, list[str], list[str]]:
    required_rank = education_rank(job.get("education_required"))
    candidate_rank = education_rank(resume.get("highest_education"))
    score = max_score * 0.7
    risks: list[str] = []
    reasons: list[str] = []
    if required_rank is None or required_rank == 0:
        score = max_score * 0.75
        reasons.append("岗位学历要求不限或未明确。")
    elif candidate_rank is None:
        score = max_score * 0.6
        reasons.append("候选人学历信息不完整，按中性分处理。")
    elif candidate_rank >= required_rank:
        score = max_score * 0.85
        reasons.append("候选人学历满足岗位要求。")
    else:
        gap = required_rank - candidate_rank
        score = max(1.0, max_score * 0.85 - gap * 2.2)
        risks.append("EDUCATION_BELOW_REQUIREMENT")
        reasons.append("候选人学历低于岗位要求，已降权。")

    major_required = to_text(job.get("major_required"), "")
    resume_major = to_text(resume.get("major"), "")
    if major_required and resume_major and not is_missing(major_required) and not is_missing(resume_major):
        major_ratio = SequenceMatcher(None, major_required, resume_major).ratio()
        if major_ratio >= 0.35 or any(word in major_required for word in [resume_major, "不限"]):
            score = min(max_score, score + max_score * 0.15)
            reasons.append("专业方向存在相关性。")
    return round(clamp(score, 0, max_score), 1), risks, reasons


def parse_years(value: Any, prefer: str) -> float | None:
    text = to_text(value, "").lower()
    if not text:
        return None
    if any(word in text for word in ["不限", "无经验", "应届", "在校"]):
        return 0.0
    numbers = [float(item) for item in re.findall(r"\d+(?:\.\d+)?", text)]
    if not numbers:
        return None
    return min(numbers) if prefer == "min" else max(numbers)


def duration_years(text: Any) -> float | None:
    value = to_text(text, "")
    if not value:
        return None
    current_year = datetime.now().year
    current_month = datetime.now().month
    month_pairs = re.findall(r"((?:19|20)\d{2})[./-](\d{1,2})", value)
    if len(month_pairs) >= 1 and ("至今" in value or "现在" in value):
        start_year, start_month = map(int, month_pairs[0])
        return max(0.0, (current_year - start_year) + (current_month - start_month) / 12)
    if len(month_pairs) >= 2:
        start_year, start_month = map(int, month_pairs[0])
        end_year, end_month = map(int, month_pairs[-1])
        return max(0.0, (end_year - start_year) + (end_month - start_month) / 12)
    years = [int(item) for item in re.findall(r"(?:19|20)\d{2}", value)]
    if "至今" in value or "现在" in value:
        years.append(current_year)
    if len(years) >= 2:
        return float(max(years) - min(years))
    return None


def infer_candidate_years(resume: dict[str, Any]) -> float | None:
    explicit = parse_years(resume.get("years_of_experience"), "max")
    if explicit is not None:
        return explicit
    total = 0.0
    for item in to_list(resume.get("work_experience")):
        if isinstance(item, dict):
            years = duration_years(item.get("duration"))
            if years is not None:
                total += years
    if total > 0:
        return round(min(total, 30), 1)
    projects = [duration_years(item.get("duration")) for item in to_list(resume.get("project_experience")) if isinstance(item, dict)]
    project_total = sum(year for year in projects if year is not None)
    if project_total > 0:
        return round(min(project_total, 3.0), 1)
    if to_list(resume.get("project_experience")):
        return 0.5
    return None


def score_experience(resume: dict[str, Any], job: dict[str, Any], max_score: float) -> tuple[float, list[str], list[str]]:
    required_years = parse_years(job.get("experience_required"), "min")
    candidate_years = infer_candidate_years(resume)
    if required_years is None or required_years == 0:
        return max_score, [], ["岗位经验要求不限或未明确。"]
    if candidate_years is None:
        return round(max_score * 0.6, 1), [], ["候选人经验年限不完整，按中性分处理。"]
    if candidate_years >= required_years:
        return max_score, [], ["候选人经验年限满足岗位要求。"]
    if candidate_years >= required_years * 0.7:
        return round(max_score * 0.7, 1), ["EXPERIENCE_BELOW_REQUIREMENT"], ["候选人经验年限略低于岗位要求，轻度降权。"]
    return round(max_score * 0.4, 1), ["EXPERIENCE_BELOW_REQUIREMENT"], ["候选人经验年限低于岗位要求，已降权。"]


def score_bucket(resume_value: Any, job_value: Any, max_score: float, risk_flag: str, label: str) -> tuple[float, list[str], list[str]]:
    resume_bucket = normalize_bucket(resume_value)
    job_bucket = normalize_bucket(job_value)
    if not resume_bucket or not job_bucket:
        return round(max_score * 0.6, 1), [], [f"{label}信息不完整，按中性分处理。"]
    if resume_bucket == job_bucket:
        return max_score, [], [f"{label}一致。"]
    return round(max_score * 0.35, 1), [risk_flag], [f"{label}不一致，已降权但不淘汰。"]


def text_similarity_score(left_text: str, right_text: str, max_score: float) -> float:
    if not left_text or not right_text:
        return round(max_score * 0.45, 1)
    ratio = SequenceMatcher(None, left_text[:1600], right_text[:1600]).ratio()
    return round(clamp(max_score * ratio * 1.8, max_score * 0.15, max_score), 1)


def score_semantic(
    resume: dict[str, Any],
    job: dict[str, Any],
    max_score: float,
    enable_embedding: bool,
) -> tuple[float | None, bool, str, float | None]:
    resume_text = build_resume_profile_text(resume)
    job_text = build_job_profile_text(job)
    if not enable_embedding:
        return None, False, "未启用 embedding，语义维度从总权重中移除。", None
    resume_vector, resume_error = call_embedding(resume_text)
    job_vector, job_error = call_embedding(job_text)
    similarity = cosine_similarity(resume_vector, job_vector)
    if similarity is None:
        error = resume_error or job_error or "向量相似度计算失败"
        return None, False, error, None
    return round(max_score * similarity, 1), True, "", round(similarity, 4)


def score_project_responsibility(resume: dict[str, Any], job: dict[str, Any], max_score: float) -> tuple[float, list[str]]:
    resume_text = flatten_text([resume.get("work_experience"), resume.get("project_experience"), resume.get("profile_summary")]).lower()
    job_items = to_list(job.get("responsibilities")) + to_list(job.get("requirements")) + to_list(job.get("keywords"))
    if not job_items:
        return round(max_score * 0.5, 1), ["岗位职责/要求不足，项目职责相关度按中性分处理。"]
    matched = 0
    for item in job_items:
        text = to_text(item, "").lower()
        key = normalize_skill_key(text)
        if text and (text in resume_text or key in resume_text):
            matched += 1
    direct_score = max_score * matched / len(job_items)
    text_score = text_similarity_score(resume_text, flatten_text(job_items).lower(), max_score)
    score = max(direct_score, text_score * 0.8)
    return round(clamp(score, 0, max_score), 1), [f"项目/职责相关度命中 {matched}/{len(job_items)}。"]


def completeness_score(data: dict[str, Any], fields: list[str], max_score: float) -> tuple[float, list[str]]:
    if not fields:
        return max_score, []
    present = 0
    for field in fields:
        value = data.get(field)
        if isinstance(value, (list, tuple, set)):
            if to_list(value):
                present += 1
        elif not is_missing(value):
            present += 1
    score = max_score * present / len(fields)
    return round(score, 1), [f"关键字段完整度 {present}/{len(fields)}。"]


def score_benefits_employment(job: dict[str, Any], max_score: float) -> tuple[float, list[str]]:
    score = max_score * 0.45
    reasons = []
    if to_list(job.get("benefits")):
        score += max_score * 0.25
        reasons.append("岗位福利信息明确。")
    if not is_missing(job.get("employment_type")):
        score += max_score * 0.15
        reasons.append("用工类型明确。")
    if not is_missing(job.get("work_mode")):
        score += max_score * 0.15
        reasons.append("办公方式明确。")
    return round(clamp(score, 0, max_score), 1), reasons or ["福利/用工信息较少，按中性分处理。"]


def score_employer_fit(
    resume: dict[str, Any],
    job: dict[str, Any],
    candidate_graph: dict[str, Any],
    job_graph: dict[str, Any],
    semantic_score: float | None,
) -> dict[str, Any]:
    scores: dict[str, float] = {}
    omitted: set[str] = set()
    risk_flags: list[str] = []
    reasons: list[str] = []

    skill_result = score_required_skills(candidate_graph, job_graph)
    scores["hard_skill_coverage"] = skill_result["coverage_score"]
    scores["evidence_path"] = skill_result["evidence_score"]
    reasons.extend(skill_result["reasons"])
    if skill_result["missing"]:
        risk_flags.append("MISSING_CORE_SKILLS")

    if semantic_score is None:
        omitted.add("semantic_similarity")
    else:
        scores["semantic_similarity"] = semantic_score

    score, score_reasons = score_project_responsibility(resume, job, EMPLOYER_WEIGHTS["project_responsibility"])
    scores["project_responsibility"] = score
    reasons.extend(score_reasons)

    score, risks, score_reasons = score_experience(resume, job, EMPLOYER_WEIGHTS["experience"])
    scores["experience"] = score
    risk_flags.extend(risks)
    reasons.extend(score_reasons)

    score, risks, score_reasons = score_education_major(resume, job, EMPLOYER_WEIGHTS["education_major"])
    scores["education_major"] = score
    risk_flags.extend(risks)
    reasons.extend(score_reasons)

    score, preferred_matched, score_reasons = score_preferred_soft(candidate_graph, job_graph, job)
    scores["preferred_soft"] = score
    reasons.extend(score_reasons)

    score, score_reasons = completeness_score(
        resume,
        ["profile_summary", "core_skills", "skill_evidence", "work_experience", "project_experience", "highest_education"],
        EMPLOYER_WEIGHTS["data_completeness"],
    )
    scores["data_completeness"] = score
    reasons.extend(score_reasons)

    total, raw_scores, factor = normalize_component_scores(scores, EMPLOYER_WEIGHTS, omitted)
    return {
        "score": total,
        "raw_scores": raw_scores,
        "normalization_factor": factor,
        "matched_skills": skill_result["matched"],
        "partial_matched_skills": skill_result["partial"],
        "missing_required_skills": skill_result["missing"],
        "matched_preferred_skills": preferred_matched,
        "evidence_paths": skill_result["paths"],
        "uncertain_relations": skill_result["uncertain"],
        "risk_flags": merge_unique(risk_flags),
        "reasons": merge_unique(reasons),
        "omitted_dimensions": sorted(omitted),
    }


def score_candidate_fit(
    resume: dict[str, Any],
    job: dict[str, Any],
    development_score: float | None,
) -> dict[str, Any]:
    scores: dict[str, float] = {}
    omitted: set[str] = set()
    risk_flags: list[str] = []
    reasons: list[str] = []

    score, risks, score_reasons = score_bucket(
        resume.get("job_family_bucket"),
        job.get("job_family_bucket"),
        CANDIDATE_WEIGHTS["job_family"],
        "JOB_FAMILY_MISMATCH",
        "岗位职能方向",
    )
    scores["job_family"] = score
    risk_flags.extend(risks)
    reasons.extend(score_reasons)

    score, risks, score_reasons = score_city(resume, job, CANDIDATE_WEIGHTS["city"])
    scores["city"] = score
    risk_flags.extend(risks)
    reasons.extend(score_reasons)

    score, risks, score_reasons = score_salary(resume, job, CANDIDATE_WEIGHTS["salary"])
    scores["salary"] = score
    risk_flags.extend(risks)
    reasons.extend(score_reasons)

    score, risks, score_reasons = score_bucket(
        resume.get("industry_bucket"),
        job.get("industry_bucket"),
        CANDIDATE_WEIGHTS["industry"],
        "INDUSTRY_MISMATCH",
        "行业赛道",
    )
    scores["industry"] = score
    risk_flags.extend(risks)
    reasons.extend(score_reasons)

    if development_score is None:
        omitted.add("development_similarity")
    else:
        scores["development_similarity"] = development_score

    score, score_reasons = score_benefits_employment(job, CANDIDATE_WEIGHTS["benefits_employment"])
    scores["benefits_employment"] = score
    reasons.extend(score_reasons)

    score, score_reasons = completeness_score(
        job,
        ["job_summary", "job_title", "city", "salary_min", "salary_max", "required_skills", "responsibilities"],
        CANDIDATE_WEIGHTS["data_completeness"],
    )
    scores["data_completeness"] = score
    reasons.extend(score_reasons)

    total, raw_scores, factor = normalize_component_scores(scores, CANDIDATE_WEIGHTS, omitted)
    return {
        "score": total,
        "raw_scores": raw_scores,
        "normalization_factor": factor,
        "risk_flags": merge_unique(risk_flags),
        "reasons": merge_unique(reasons),
        "omitted_dimensions": sorted(omitted),
    }


def recommend_level(score: float) -> str:
    if score >= 80:
        return "HIGH"
    if score >= 65:
        return "MEDIUM"
    if score >= 50:
        return "LOW"
    return "NOT_RECOMMENDED"


def suitability_status(score: float, risk_flags: list[str]) -> tuple[bool, str]:
    major_risks = {
        "MISSING_CORE_SKILLS",
        "JOB_FAMILY_MISMATCH",
        "CITY_MISMATCH",
        "SALARY_NOT_OVERLAP",
        "EDUCATION_BELOW_REQUIREMENT",
        "EXPERIENCE_BELOW_REQUIREMENT",
    }
    major_risk_count = len(set(risk_flags).intersection(major_risks))
    if score >= SUITABLE_SCORE_THRESHOLD and not (score < 75 and major_risk_count >= 2):
        return True, ""
    if "MISSING_CORE_SKILLS" in risk_flags and score < SUITABLE_SCORE_THRESHOLD:
        return False, "最高匹配分低于65，且存在岗位硬性技能缺口，暂不作为强推荐。"
    if "MISSING_CORE_SKILLS" in risk_flags:
        return False, "存在岗位硬性技能缺口，暂不作为强推荐。"
    if major_risk_count >= 2:
        return False, "匹配分虽接近阈值，但存在多项关键风险，暂不作为强推荐。"
    return False, "最高匹配分低于65，当前岗位池暂无强推荐结果。"


def compact_graph(graph: dict[str, Any], limit_nodes: int = 40, limit_edges: int = 80) -> dict[str, Any]:
    return {
        "nodes": graph.get("nodes", [])[:limit_nodes],
        "edges": graph.get("edges", [])[:limit_edges],
        "nodeCount": len(graph.get("nodes", [])),
        "edgeCount": len(graph.get("edges", [])),
    }


def graph_summary(candidate_graph: dict[str, Any], job_graph: dict[str, Any], evidence_paths: list[dict[str, Any]]) -> dict[str, Any]:
    return {
        "candidateGraph": compact_graph(candidate_graph),
        "jobGraph": compact_graph(job_graph),
        "topEvidencePaths": evidence_paths[:10],
    }


def rule_based_summary(result: dict[str, Any]) -> str:
    matched = result.get("matched_skills", [])
    partial = result.get("partial_matched_skills", [])
    missing = result.get("missing_required_skills", [])
    if result.get("is_suitable") and matched:
        return f"匹配基础较强：命中核心技能{'、'.join(matched[:5])}，总分{result['match_score']}。"
    if matched or partial:
        skills = matched[:4] + partial[:2]
        return f"存在部分匹配：关联技能{'、'.join(skills)}，但仍需关注缺口{'、'.join(missing[:4]) or '未明确'}。"
    if missing:
        return f"不作为强推荐：缺少岗位硬性技能{'、'.join(missing[:5])}，总分{result['match_score']}。"
    return result.get("no_strong_match_reason") or "当前岗位池暂无强推荐结果。"


def rule_based_suggestions(result: dict[str, Any]) -> list[str]:
    missing = result.get("missing_required_skills", [])
    matched = result.get("matched_skills", [])
    if missing:
        return [f"优先补充或人工确认这些硬性技能：{'、'.join(missing[:5])}。"]
    if matched:
        return [f"面试或推荐展示时重点验证这些命中技能的项目深度：{'、'.join(matched[:5])}。"]
    return ["建议补充更具体的项目经历、技能证据和岗位要求后再进行复核。"]


def valid_llm_text(value: Any) -> bool:
    text = to_text(value, "")
    if not text or text in INVALID_LLM_PHRASES:
        return False
    return not any(phrase in text for phrase in INVALID_LLM_PHRASES)


def sanitize_llm_text(value: Any, fallback: str) -> str:
    return to_text(value) if valid_llm_text(value) else fallback


def sanitize_llm_list(value: Any) -> list[str]:
    return [
        text
        for text in merge_unique(to_list(value))
        if valid_llm_text(text) and not any(fragment in text for fragment in GENERIC_LLM_FRAGMENTS)
    ]


def summary_has_evidence(summary: str, result: dict[str, Any]) -> bool:
    evidence_terms = (
        result.get("matched_skills", [])
        + result.get("partial_matched_skills", [])
        + result.get("missing_required_skills", [])
        + result.get("risk_flags", [])
    )
    return any(term and term in summary for term in evidence_terms)


def build_llm_prompt(result: dict[str, Any], uncertain_relations: list[dict[str, Any]]) -> str:
    context = {
        "match_result": {
            "algorithm_score": result["algorithm_score"],
            "employer_fit_score": result["employer_fit_score"],
            "candidate_fit_score": result["candidate_fit_score"],
            "dimension_scores": result["dimension_scores"],
            "matched_skills": result["matched_skills"],
            "partial_matched_skills": result["partial_matched_skills"],
            "missing_required_skills": result["missing_required_skills"],
            "risk_flags": result["risk_flags"],
            "evidence_paths": result["evidence_paths"][:8],
            "is_suitable": result["is_suitable"],
        },
        "uncertain_relations": uncertain_relations[:8],
    }
    return f"""
你是软件杯“AI智能匹配与能力图谱系统”的匹配解释专家。
请只基于给定的算法结果和证据路径做复核解释。

硬性要求：
- 不能重新给总分，不能声称分数由你决定。
- llm_adjustment 只能在 -5 到 5 之间，用于轻微修正算法分。
- ai_summary、ai_risks、ai_suggestions 必须引用 matched_skills、missing_required_skills、risk_flags 或 evidence_paths 中的证据。
- relation_audits 只能评价 uncertain_relations 中出现的关系，取值 equivalent/related/weak/not_related。
- 不要编造候选人或岗位不存在的技能、项目、证书。
- 只返回合法 JSON，不要 Markdown。

返回字段：
{{
  "llm_adjustment": 0,
  "ai_summary": "",
  "ai_risks": [],
  "ai_suggestions": [],
  "relation_audits": [
    {{"required_skill": "", "candidate_skill": "", "judgement": "related", "reason": ""}}
  ]
}}

【输入】
{json.dumps(context, ensure_ascii=False)}
"""


def apply_llm_review(result: dict[str, Any], uncertain_relations: list[dict[str, Any]]) -> dict[str, Any]:
    try:
        review = call_llm(build_llm_prompt(result, uncertain_relations))
        adjustment = number_or_none(review.get("llm_adjustment")) or 0.0
        adjustment = clamp(adjustment, -5.0, 5.0)
        result["llm_adjustment"] = round(adjustment, 1)
        result["llm_used"] = True
        result["match_score"] = round(clamp(result["algorithm_score"] + adjustment), 1)
        result["recommend_level"] = recommend_level(result["match_score"])
        is_suitable, reason = suitability_status(result["match_score"], result["risk_flags"])
        result["is_suitable"] = is_suitable
        result["no_strong_match_reason"] = reason

        summary = sanitize_llm_text(review.get("ai_summary"), rule_based_summary(result))
        if not summary_has_evidence(summary, result):
            summary = rule_based_summary(result)
        suggestions = sanitize_llm_list(review.get("ai_suggestions")) or rule_based_suggestions(result)
        result["ai_summary"] = summary
        result["ai_risks"] = sanitize_llm_list(review.get("ai_risks"))
        result["ai_suggestions"] = suggestions
        result["relation_audits"] = to_list(review.get("relation_audits"))[:8]
    except Exception as exc:
        result["llm_used"] = False
        result["llm_adjustment"] = 0.0
        result["risk_flags"] = merge_unique(result["risk_flags"] + ["LLM_REVIEW_UNAVAILABLE"])
        result["ai_summary"] = rule_based_summary(result)
        result["ai_risks"] = [f"大模型复核未启用或失败：{to_text(exc)[:120]}"]
        result["ai_suggestions"] = rule_based_suggestions(result)
    return result


def get_resume_id(resume: dict[str, Any], index: int) -> str:
    return first_present(resume.get("resume_id"), resume.get("id"), default=f"resume_{index + 1}")


def get_job_id(job: dict[str, Any], index: int) -> str:
    return first_present(job.get("job_id"), job.get("id"), default=f"job_{index + 1}")


def combine_score(employer_score: float, candidate_score: float, direction: str) -> tuple[float, str]:
    blend = RESUME_TO_JOBS_BLEND if direction == "resume_to_jobs" else JOB_TO_RESUMES_BLEND
    score = employer_score * blend["employer"] + candidate_score * blend["candidate"]
    formula = (
        f"match_score = employer_fit_score * {blend['employer']} "
        f"+ candidate_fit_score * {blend['candidate']}"
    )
    return round(clamp(score), 1), formula


def match_pair_v2(
    resume: dict[str, Any],
    job: dict[str, Any],
    resume_id: str,
    job_id: str,
    direction: str,
    enable_embedding: bool,
    include_trace: bool,
) -> dict[str, Any]:
    candidate_graph = build_candidate_graph(resume, resume_id)
    job_graph = build_job_graph(job, job_id)
    semantic_score, embedding_used, embedding_error, semantic_similarity = score_semantic(
        resume,
        job,
        EMPLOYER_WEIGHTS["semantic_similarity"],
        enable_embedding,
    )
    development_score = None if semantic_score is None else round(CANDIDATE_WEIGHTS["development_similarity"] * (semantic_similarity or 0), 1)

    employer = score_employer_fit(resume, job, candidate_graph, job_graph, semantic_score)
    candidate = score_candidate_fit(resume, job, development_score)
    algorithm_score, formula = combine_score(employer["score"], candidate["score"], direction)
    risk_flags = merge_unique(employer["risk_flags"] + candidate["risk_flags"])
    if enable_embedding and not embedding_used:
        risk_flags.append("SEMANTIC_EMBEDDING_UNAVAILABLE")

    is_suitable, no_strong_reason = suitability_status(algorithm_score, risk_flags)
    evidence_paths = employer["evidence_paths"][:12] if include_trace else []
    result = {
        "rank": 0,
        "resume_id": resume_id,
        "job_id": job_id,
        "name": first_present(resume.get("name"), default="未提及"),
        "expected_position": display_text(resume.get("expected_position"), default="未提及"),
        "job_title": first_present(job.get("job_title"), job.get("title"), default="未提及"),
        "company_name": first_present(job.get("company_name"), job.get("company"), default="未提及"),
        "match_score": algorithm_score,
        "algorithm_score": algorithm_score,
        "employer_fit_score": employer["score"],
        "candidate_fit_score": candidate["score"],
        "llm_adjustment": 0.0,
        "llm_used": False,
        "embedding_used": embedding_used,
        "embedding_similarity": semantic_similarity,
        "is_suitable": is_suitable,
        "no_strong_match_reason": no_strong_reason,
        "recommend_level": recommend_level(algorithm_score),
        "dimension_scores": {
            "employer": employer["raw_scores"],
            "candidate": candidate["raw_scores"],
            "normalization": {
                "employer_factor": employer["normalization_factor"],
                "candidate_factor": candidate["normalization_factor"],
                "employer_omitted": employer["omitted_dimensions"],
                "candidate_omitted": candidate["omitted_dimensions"],
            },
        },
        "matched_skills": employer["matched_skills"],
        "partial_matched_skills": employer["partial_matched_skills"],
        "missing_required_skills": employer["missing_required_skills"],
        "matched_preferred_skills": employer["matched_preferred_skills"],
        "risk_flags": risk_flags,
        "evidence_paths": evidence_paths,
        "graph_summary": graph_summary(candidate_graph, job_graph, evidence_paths) if include_trace else {},
        "score_formula": formula,
        "reasons": merge_unique(employer["reasons"] + candidate["reasons"] + ([embedding_error] if embedding_error else [])),
        "ai_summary": "",
        "ai_risks": [],
        "ai_suggestions": [],
        "relation_audits": [],
    }
    result["ai_summary"] = rule_based_summary(result)
    result["ai_suggestions"] = rule_based_suggestions(result)
    result["_uncertain_relations"] = employer["uncertain_relations"]
    return result


def finalize_ranks(results: list[dict[str, Any]]) -> list[dict[str, Any]]:
    results.sort(key=lambda item: item["match_score"], reverse=True)
    for index, item in enumerate(results, start=1):
        item["rank"] = index
        item.pop("_uncertain_relations", None)
    return results


def build_result_payload(results: list[dict[str, Any]]) -> dict[str, Any]:
    best_score = results[0]["match_score"] if results else 0
    has_strong_match = bool(results and results[0].get("is_suitable"))
    no_strong_match_reason = "" if has_strong_match else "当前结果为空或最高匹配分低于65，暂无强推荐。"
    if results and not has_strong_match:
        no_strong_match_reason = results[0].get("no_strong_match_reason") or no_strong_match_reason
    return {
        "total": len(results),
        "hasStrongMatch": has_strong_match,
        "bestScore": best_score,
        "noStrongMatchReason": no_strong_match_reason,
        "results": results,
    }


def match_resume_to_jobs(
    resume: dict[str, Any],
    jobs: list[dict[str, Any]],
    enable_llm: bool = True,
    llm_top_k: int = 20,
    enable_embedding: bool = True,
    include_trace: bool = True,
) -> list[dict[str, Any]]:
    pairs: list[dict[str, Any]] = []
    resume_id = get_resume_id(resume, 0)
    for index, job in enumerate(jobs):
        result = match_pair_v2(
            resume,
            job,
            resume_id,
            get_job_id(job, index),
            "resume_to_jobs",
            enable_embedding,
            include_trace,
        )
        pairs.append({"result": result})

    pairs.sort(key=lambda item: item["result"]["algorithm_score"], reverse=True)
    if enable_llm and llm_top_k > 0:
        for item in pairs[: min(llm_top_k, MAX_LLM_TOP_K)]:
            item["result"] = apply_llm_review(item["result"], item["result"].get("_uncertain_relations", []))
    return finalize_ranks([item["result"] for item in pairs])


def match_job_to_resumes(
    job: dict[str, Any],
    resumes: list[dict[str, Any]],
    enable_llm: bool = True,
    llm_top_k: int = 20,
    enable_embedding: bool = True,
    include_trace: bool = True,
) -> list[dict[str, Any]]:
    pairs: list[dict[str, Any]] = []
    job_id = get_job_id(job, 0)
    for index, resume in enumerate(resumes):
        result = match_pair_v2(
            resume,
            job,
            get_resume_id(resume, index),
            job_id,
            "job_to_resumes",
            enable_embedding,
            include_trace,
        )
        pairs.append({"result": result})

    pairs.sort(key=lambda item: item["result"]["algorithm_score"], reverse=True)
    if enable_llm and llm_top_k > 0:
        for item in pairs[: min(llm_top_k, MAX_LLM_TOP_K)]:
            item["result"] = apply_llm_review(item["result"], item["result"].get("_uncertain_relations", []))
    return finalize_ranks([item["result"] for item in pairs])


@app.get("/")
async def health_check():
    return {
        "status": "GREEN",
        "service": "kyc-talent-job-match-v2",
        "version": "2.0.0",
        "port": PORT,
        "endpoints": {
            "resumeToJobs": "/api/ai/match/resume-to-jobs",
            "jobToResumes": "/api/ai/match/job-to-resumes",
        },
        "llmEnabledByConfig": bool(DASHSCOPE_API_KEY),
        "embeddingModel": EMBEDDING_MODEL,
        "maxLlmTopK": MAX_LLM_TOP_K,
    }


@app.post("/api/ai/match/resume-to-jobs")
async def resume_to_jobs(request: ResumeToJobsRequest):
    if not request.resume:
        return api_response(400, False, "resume 不能为空")
    if not request.jobs:
        return api_response(400, False, "jobs 不能为空")
    results = match_resume_to_jobs(
        request.resume,
        request.jobs,
        request.enableLLM,
        min(request.llmTopK, MAX_LLM_TOP_K),
        request.enableEmbedding,
        request.includeTrace,
    )
    return api_response(200, True, "匹配成功", build_result_payload(results))


@app.post("/api/ai/match/job-to-resumes")
async def job_to_resumes(request: JobToResumesRequest):
    if not request.job:
        return api_response(400, False, "job 不能为空")
    if not request.resumes:
        return api_response(400, False, "resumes 不能为空")
    results = match_job_to_resumes(
        request.job,
        request.resumes,
        request.enableLLM,
        min(request.llmTopK, MAX_LLM_TOP_K),
        request.enableEmbedding,
        request.includeTrace,
    )
    return api_response(200, True, "匹配成功", build_result_payload(results))


if __name__ == "__main__":
    uvicorn.run("match_v2_api:app", host="0.0.0.0", port=PORT, reload=False)
