package com.xgj.devpulse.redisTest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTest {
    private final StringRedisTemplate redis;

    public void setUserNameTest(Integer userId, String realName) {

        String key = String.format("user:%d:name", userId);
        redis.opsForValue().set(key, realName);
        // 设置到期时间
        redis.expire(key, 20, TimeUnit.SECONDS);

    }

    public String getUserNameTest(Integer userId){
        String key = String.format("user:%d:name", userId);

        return redis.opsForValue().get(key);
    }

}
