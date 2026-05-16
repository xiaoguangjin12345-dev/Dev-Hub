package com.xgj.devpulse.AITest;

import com.xgj.devpulse.AITest.DTO.OpenAIRequestDTO;
import com.xgj.devpulse.rabbitmqTest.config.RabbitTestConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIRequestTest {
    private final RabbitTemplate rabbit;

    public void request(List<OpenAIRequestDTO.Msg> messages) {

        // 组装请求体
        OpenAIRequestDTO msg = new OpenAIRequestDTO();
        msg.setModel("qwen-plus");
        msg.setMessages(messages);

        // 将请求发送到队列
        rabbit.convertAndSend(RabbitTestConfig.AI_service_test, msg);

    }

//    public void request(String userMessage){
//
//        // 组装请求体
//        OpenAIRequestDTO msg = new OpenAIRequestDTO();
//        msg.setModel("qwen-plus");
//        msg.setMessages(Collections.singletonList(new OpenAIRequestDTO.Msg("user", userMessage)));
//
//        // 将请求发送到队列
//        rabbit.convertAndSend(RabbitConfig.AI_service, msg);
//
//    }
}
