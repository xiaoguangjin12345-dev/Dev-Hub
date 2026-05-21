import httpx

from typing import TypeVar

from core.config import settings

T = TypeVar('T')

# 通过Java的接口，写Redis
async def set_redis(redis_key: str, redis_ttl, status: int, data: T):
    url = settings.java_client_url
    request_body = {
        "redisKey": redis_key,
        "redisTtl": redis_ttl,
        "status": status,
        "data": data
    }
    # 请求Java后端
    async with httpx.AsyncClient() as client:
        await client.post(url, json = request_body)