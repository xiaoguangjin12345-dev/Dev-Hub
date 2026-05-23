import concurrent.futures
from concurrent.futures import ThreadPoolExecutor

import httpx
from typing import TypeVar, Optional

T = TypeVar('T')

# 定义连接池client（异步、同步）
_client_async: Optional[httpx.AsyncClient] = None
_client_sync: Optional[httpx.Client] = None

# 定义全局线程池，用于token使用日志写入
_log_thread_pool: Optional[ThreadPoolExecutor] = None



# 构造全局单例client（异步）
def get_java_client_async() -> httpx.AsyncClient:
    global _client_async
    if _client_async is None or _client_async.is_closed:
        # 这里配置长连接参数
        _client_async = httpx.AsyncClient(
            timeout=10.0,
            limits=httpx.Limits(max_connections=100, max_keepalive_connections=20)
        )
    return _client_async

# 应用关闭时调用，关闭连接（异步）
async def close_java_client_async():
    global _client_async
    if _client_async:
        await _client_async.aclose()



# 构造全局单例client（同步）
def get_java_client_sync() -> httpx.Client:
    global _client_sync
    if _client_sync is None or _client_sync.is_closed:
        # 这里配置长连接参数
        _client_sync = httpx.Client(
            timeout=10.0,
            limits=httpx.Limits(max_connections=100, max_keepalive_connections=20)
        )
    return _client_sync

# 应用关闭时调用，关闭连接（同步）
def close_java_client_sync():
    global _client_sync
    if _client_sync:
        _client_sync.close()



# 构造全局单例线程池（token日志用）
def get_log_thread_pool() -> ThreadPoolExecutor:
    global _log_thread_pool
    if _log_thread_pool is None:
        # 线程池
        _log_thread_pool = concurrent.futures.ThreadPoolExecutor(
            max_workers=5,
            thread_name_prefix="LogWriter"
        )

    return _log_thread_pool

# 应用关闭时调用，关闭线程池（token日志用）
def close_log_thread_pool():
    global _log_thread_pool
    if _log_thread_pool:
        _log_thread_pool.shutdown(wait=True)



