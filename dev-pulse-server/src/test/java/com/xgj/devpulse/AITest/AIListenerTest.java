package com.xgj.devpulse.AITest;

import com.xgj.devpulse.AITest.DTO.OpenAIRequestDTO;
import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.rabbitmqTest.config.RabbitTestConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitTestConfig.AI_service_test)
@RequiredArgsConstructor
public class AIListenerTest {
    private final AIServiceTest aiService;
    private final RedisService redisService;

    @RabbitHandler
    public void receive(OpenAIRequestDTO msg) {
        // 获取结果
        String result = aiService.response(msg);
        redisService.set("ai:content", result);
        redisService.set("ai:status", 2);
    }
}
