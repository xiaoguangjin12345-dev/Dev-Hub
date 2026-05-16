import threading

from fastapi import FastAPI
import uvicorn
from api.endpoints import router
from listener.task_assign_listener import start_listening

app = FastAPI()
# 把分路由注册到主应用上
app.include_router(router, prefix="/v1/ai")

if __name__ == "__main__":
    # 创建独立的线程运行监听器
    listener_thread = threading.Thread(target=start_listening, daemon=True)
    listener_thread.start()

    uvicorn.run(app, host="127.0.0.1", port=8000)     # 预留FastAPI服务，目前没有任何接口