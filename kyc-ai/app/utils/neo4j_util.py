from __future__ import annotations
from contextlib import contextmanager
from neo4j import GraphDatabase, Driver
from app.config.settings import settings


class Neo4jUtil:
    """
    Neo4j 图数据库高可用工具类
    """

    # 全局复用同一个底层连接池驱动（Neo4j 官方强力规约：Driver 是线程安全的，全局复用一个即可）
    _driver: Driver | None = None

    @staticmethod
    def get_driver() -> Driver:
        """
        单例获取或初始化全局唯一图数据库连接驱动底座
        """
        if Neo4jUtil._driver is None:
            try:
                # 建立图数据库大驱动，内部自带高并发连接池
                Neo4jUtil._driver = GraphDatabase.driver(
                    settings.neo4j_uri,
                    auth=(settings.neo4j_user, settings.neo4j_password)
                )
            except Exception as e:
                print(f"❌ [Neo4j图数据库] 初始化全局驱动断裂，请检查 .env 账密或安全组: {str(e)}")
                raise e
        return Neo4jUtil._driver

    @staticmethod
    @contextmanager
    def session_context():
        """
        高级会话上下文管理器 (类似于 Java 的 try-with-resources)
        利用 with 语法确保图数据库事务正常流转，且不论如何执行，最后都必定 close 释放会话，绝不积压内存
        """
        driver = Neo4jUtil.get_driver()
        session = None
        try:
            # 建立一个会话空间
            session = driver.session()
            yield session
        except Exception as e:
            print(f"❌ [Neo4j图数据库] Cypher 图图语法事务执行失败: {str(e)}")
            raise e
        finally:
            if session:
                session.close()  # 🌟 终极护航：无条件释放当前连接回池子

    @staticmethod
    def close_global_driver():
        """
        当整个 Python 进程（如守护消费者）退出时，优雅拔掉插头、关闭全局大连接池
        """
        if Neo4jUtil._driver:
            Neo4jUtil._driver.close()
            Neo4jUtil._driver = None
            print("🔌 [Neo4j图数据库] 全局连接池已安全优雅关闭。")


if __name__ == "__main__":
    # 🧪 测试 Neo4j 数据库是否在公网联通
    print("▶️ 开始测试 Neo4j 远程图数据库联调...")
    try:
        # 使用我们刚封装好的上下文管理器
        with Neo4jUtil.session_context() as session:

            # 执行一句图数据库自带的经典基础性能自检：查询服务器当前的数据库版本和协议
            def get_version(tx):
                result = tx.run("CALL dbms.components() YIELD name, versions RETURN name, versions")
                return result.peek()


            row = session.execute_read(get_version)

            print(f"🎉 [Neo4j图数据库] 连通测试大成功！")
            print(f"🤖 当前图组件名称: {row['name']} | 版本号: {row['versions'][0]}")

    except Exception as e:
        print(f"💥 测试失败，原因请抬头看控制台报错日志。")