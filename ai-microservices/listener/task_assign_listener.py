import json
import asyncio
from concurrent.futures import ThreadPoolExecutor

import aio_pika
from aio_pika import IncomingMessage

from core.config import settings
from services.task_assign_service import execute_task_assign

# 调用AI任务拆解模块
async def on_message(message: IncomingMessage):
    async with message.process():
        msg_data = json.loads(message.body.decode())
        # 直接在异步上下文中调用
        await execute_task_assign(msg_data)

# 监听AI请求消息
async def start_listening_async():
    # 异步连接 RabbitMQ
    connection = await aio_pika.connect_robust(
        host=settings.rabbitmq_host,
        port=settings.rabbitmq_port,
        login=settings.rabbitmq_user,
        password=settings.rabbitmq_password,
        virtualhost='/'
    )

    async with connection:
        channel = await connection.channel()
        await channel.set_qos(prefetch_count=5)

        queue = await channel.declare_queue('sys.ai.request.queue', durable=True)
        await queue.consume(on_message)

        # 保持运行
        await asyncio.Future()
