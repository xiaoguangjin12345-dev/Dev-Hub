import json
import asyncio
from concurrent.futures import ThreadPoolExecutor
import pika
from core.config import settings
from services.task_assign_service import execute_task_assign

# 允许同时处理5个AI任务
executor = ThreadPoolExecutor(max_workers = 5)

# 接收队列请求
def on_message(ch, method, properties, msg_str):
    message = json.loads(msg_str)
    # 任务进入线程池
    executor.submit(run_worker, ch, method, message)

# 调用AI任务拆解模块并返回ack
def run_worker(ch, method, message):
    try:
        # 在独立线程中运行任务拆解模块
        asyncio.run(execute_task_assign(message))
    except Exception as e:
        raise Exception("AI任务拆解服务失败", e)
    finally:
        # 使用add_callback_threadsafe发送ack
        ch.connection.add_callback_threadsafe(
            lambda: ch.basic_ack(delivery_tag=method.delivery_tag)
        )


# 连接RabbitMQ
def start_listening():
    # 创建身份验证对象
    credentials = pika.PlainCredentials(
        username = settings.rabbitmq_user,
        password = settings.rabbitmq_password
    )

    # 将credentials加入连接参数
    parameters = pika.ConnectionParameters(
        host = settings.rabbitmq_host,
        port = settings.rabbitmq_port,
        virtual_host='/',
        credentials=credentials
    )

    # 创建连接对象
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()

    # 确保队列存在
    channel.queue_declare(queue='sys.ai.request.queue', durable=True)

    channel.basic_qos(prefetch_count=5)
    channel.basic_consume(queue='sys.ai.request.queue', on_message_callback=on_message)
    channel.start_consuming()

