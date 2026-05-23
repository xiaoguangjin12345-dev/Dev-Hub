import asyncio
from contextlib import asynccontextmanager

from fastapi import FastAPI
import uvicorn
from api.endpoints import router
from client.client_config import close_java_client_async, close_java_client_sync, close_log_thread_pool
from listener.task_assign_listener import start_listening_async


# 定义生命周期管理器
@asynccontextmanager
async def lifespan(app: FastAPI):
    # 启动后台异步任务，监听AI请求消息
    listener_task = asyncio.create_task(start_listening_async())

    yield

    # 关闭监听器、连接池与线程池
    listener_task.cancel()
    await close_java_client_async()
    close_java_client_sync()
    close_log_thread_pool()


# 将lifespan传入FastAPI 实例
app = FastAPI(lifespan=lifespan)

# 注册路由
app.include_router(router, prefix="/v1/ai")

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)