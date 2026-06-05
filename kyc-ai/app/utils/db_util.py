import dmPython
from contextlib import contextmanager
from app.config.settings import settings


class DBUtil:
    """
    达梦数据库工具类
    """

    @staticmethod
    def get_raw_connection():
        """
        获取一个原生的达梦数据库连接，并自动切换到指定的 SCHEMA
        """
        try:
            # 1. 建立基础连接 (数据全部来自你在 .env 中配置的阿里云或本地达梦)
            conn = dmPython.connect(
                user=settings.db_user,
                password=settings.db_password,
                server=settings.db_host,
                port=settings.db_port
            )
            cursor = conn.cursor()

            # 🌟 达梦标准规约：显式切换到你在 .env 里配好的模式（大写 KYC）
            cursor.execute(f"SET SCHEMA {settings.db_schema}")
            cursor.close()

            return conn
        except Exception as e:
            print(f"❌ [达梦数据库] 建立连接失败，请检查 .env 配置、网络开放情况或系统 PATH 驱动: {str(e)}")
            raise e

    @staticmethod
    @contextmanager
    def connection_context():
        """
        高级上下文管理器 (类似于 Java 的 try-with-resources)
        确保任何时候（哪怕 SQL 报错崩溃），连接也绝对会在最后被优雅 close，防止连接池被榨干
        """
        conn = None
        try:
            conn = DBUtil.get_raw_connection()
            yield conn
            # 如果没有报错，外层业务执行完后，自动提交事务
            conn.commit()
        except Exception as e:
            if conn:
                conn.rollback()  # 发生异常，果断回滚事务
            print(f"❌ [达梦数据库] 事务执行失败已自动回滚: {str(e)}")
            raise e
        finally:
            if conn:
                conn.close()  # 🌟 终极护航：无论如何，关闭连接


if __name__ == "__main__":
    # 🧪 测试达梦数据库是否连通
    print("▶️ 开始测试达梦数据库连接...")
    try:
        # 使用我们写好的上下文管理器
        with DBUtil.connection_context() as conn:
            cursor = conn.cursor()

            # 随便查一个系统表或者你的简历表，看看能不能通
            # 这里先用一个达梦自带的查询当前用户的语句测试
            cursor.execute("SELECT USER FROM DUAL")
            result = cursor.fetchone()

            print(f"🎉 [达梦数据库] 连接测试完美成功！当前数据库登录用户为: {result[0]}")
            cursor.close()

    except Exception as e:
        print(f"💥 测试失败，原因请看上方报错日志。")