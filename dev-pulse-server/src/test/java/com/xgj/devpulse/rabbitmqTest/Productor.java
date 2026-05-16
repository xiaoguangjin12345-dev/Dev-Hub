package com.xgj.devpulse.rabbitmqTest;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Productor {
    private final RabbitTemplate rabbit;

    public void send(String message) {
        rabbit.convertAndSend("sys.log.queue", message);
    }

}
