from concurrent.futures import thread
import time

from client.client_config import get_java_client_async, get_java_client_sync

from typing import TypeVar
from openai.types.chat import ChatCompletion
from core.config import settings

T = TypeVar('T')

# 通过Java的接口，写Redis
async def set_redis(redis_key: str, redis_ttl, status: int, data: T):
    url = settings.java_client_url + "/redis"
    request_body = {
        "redisKey": redis_key,
        "redisTtl": redis_ttl,
        "status": status,
        "data": data
    }
    # 请求Java后端
    client = get_java_client_async()
    await client.post(url, json = request_body)

# 写入Token使用记录
def insert_token_usage_log(user_id: int, model_name: str, task_type: str, request: ChatCompletion):
    url = settings.java_client_url + "/database/token"
    request_body = {
        "userId": user_id,
        "modelName": model_name,
        "responseId": request.id,
        "type": task_type,
        "promptTokens": request.usage.prompt_tokens,
        "cachedTokens": request.usage.prompt_tokens_details.cached_tokens
                        if request.usage.prompt_tokens_details.cached_tokens is not None
                        else 0,
        "completionTokens": request.usage.completion_tokens,
        "totalTokens": request.usage.total_tokens
    }
    # 使用同步 Client
    client = get_java_client_sync()
    client.post(url, json = request_body)