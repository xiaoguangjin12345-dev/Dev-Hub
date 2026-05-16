package com.xgj.devpulse.rabbitmqTest.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTestConfig {
    public static final String AI_service_test = "sys.ai_service_test.queue";

    // AI服务测试队列
    @Bean
    public Queue AIServiceTestQueue(){
        return new Queue(AI_service_test);
    }
}
