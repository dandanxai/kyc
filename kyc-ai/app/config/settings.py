from __future__ import annotations

import os
from typing import Optional

from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    # 1. Java 基础网关地址
    java_base_url: str = "http://127.0.0.1:48080"

    # 2. MQ 规约
    rabbitmq_host: str = "127.0.0.1"
    rabbitmq_port: int = 5672
    rabbitmq_user: str = "guest"
    rabbitmq_password: str = "guest"
    resume_queue_name: str = "KYC_RESUME_PARSE_QUEUE"
    position_queue_name: str = "KYC_POSITION_PARSE_QUEUE"

    # 3. AI 大模型规约
    ollama_api_url: str = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
    model_name: str = "qwen-plus"
    dashscope_api_key: str = ""

    # 4. 达梦数据库专属字段规约
    db_host: str = "127.0.0.1"
    db_port: int = 5236
    db_user: str = "SYSDBA"
    db_password: str = "Dameng123"
    db_schema: str = "KYC"

    # 5. 🛠️ 新增：网络请求代理规约（默认跳过代理，直连国内大厂）
    http_proxy: Optional[str] = None
    https_proxy: Optional[str] = None

    # 6. 🌌 新增：Neo4j 图数据库专属规约
    neo4j_uri: str = "bolt://127.0.0.1:7687"
    neo4j_user: str = "neo4j"
    neo4j_password: str = "20050106@Hs"

    @property
    def request_proxies(self) -> dict:
        """
        动态拼装给 requests 库使用的代理字典
        """
        return {
            "http": self.http_proxy,
            "https": self.https_proxy
        }

    # 告诉框架去项目根目录下自动抓取 .env 文件并覆盖上面的默认值
    model_config = SettingsConfigDict(
        env_file=os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), '.env'),
        env_file_encoding='utf-8',
        extra='ignore'
    )

# 实例化全局配置对象
settings = Settings()