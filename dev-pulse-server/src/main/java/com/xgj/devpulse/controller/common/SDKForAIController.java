package com.xgj.devpulse.controller.common;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.pojo.dto.ai.AIRedisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/sdk")
@RequiredArgsConstructor
public class SDKForAIController {
    private final RedisService redisService;

    // AI服务写Redis的专用接口
    @PostMapping("/ai/redis")
    public <T> APIResponse<Boolean> setRedis(@RequestBody AIRedisDTO<T> dto) {
        // 写入Redis状态
        redisService.hashSet(dto.getRedisKey(), "data", dto.getData());
        redisService.hashSet(dto.getRedisKey(), "resultTime", LocalDateTime.now());
        redisService.hashSet(dto.getRedisKey(), "status", dto.getStatus());
        // 设置TTL
        redisService.expire(dto.getRedisKey(), dto.getRedisTtl());

        return APIResponse.success(Boolean.TRUE, "Redis更新成功");
    }
}
