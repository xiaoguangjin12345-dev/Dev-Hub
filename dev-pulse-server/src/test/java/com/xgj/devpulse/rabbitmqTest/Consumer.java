package com.xgj.devpulse.rabbitmqTest;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @RabbitListener(queues = "sys.log.queue")
    public void receive(String msg) {
        System.out.println("接收消息: " + msg);
    }

}
