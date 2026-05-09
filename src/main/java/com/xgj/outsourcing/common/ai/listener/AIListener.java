package com.xgj.outsourcing.common.ai.listener;

import com.xgj.outsourcing.common.ai.AIHttpExecutor;
import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.common.mq.RabbitConfig;
import com.xgj.outsourcing.pojo.dto.ai.AIChatMsg;
import com.xgj.outsourcing.pojo.dto.ai.AIToolCallMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = RabbitConfig.AI_service,
        containerFactory = RabbitConfig.AI_container_factory)
@RequiredArgsConstructor
public class AIListener {
    private final AIHttpExecutor aiHttpExecutor;

    // 接收文本请求并获取结果
    @RabbitHandler
    public void receiveChatResult(AIChatMsg msg) {
        try{
            // 调用Response模块
            aiHttpExecutor.chatResponse(msg);

        }catch (Exception e){
            log.error("AI文本请求服务失败", e);
        }
    }

    // 接收ToolCall请求并调用Response模块处理
    @RabbitHandler
    public void receiveToolCallResult(AIToolCallMsg msg) {
        try{
            // 调用Response模块
            aiHttpExecutor.toolCallResponse(msg);

        }catch (Exception e){
            log.error("AI ToolCall请求服务失败", e);
        }
    }

}
