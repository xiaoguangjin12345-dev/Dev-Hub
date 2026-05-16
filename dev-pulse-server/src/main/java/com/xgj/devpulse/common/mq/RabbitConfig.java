package com.xgj.devpulse.common.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {
    public static final String actual_hour_log = "sys.hour-log.queue";
    public static final String stats_data = "sys.stats_data.queue";

    // AI服务（发给Python端）
    public static final String AI_request_queue = "sys.ai.request.queue";
    public static final String AI_exchange = "sys.ai.exchange";                // 交换机名称
    public static final String AI_routing_key = "ai.request.routing";          // 路由键

    // 统计分析与工时填报的服务队列工厂
    public static final String stats_container_factory = "statsContainerFactory";
    public static final String hour_log_submit_container_factory = "hourLogSubmitContainerFactory";


    // 实际工时填报队列
    @Bean
    public Queue actualHourLogQueue(){
        return new Queue(actual_hour_log);
    }

    // 统计分析数据队列
    @Bean
    public Queue statsDataQueue(){
        return new Queue(stats_data);
    }


    // AI服务队列
    @Bean
    public Queue aiRequestQueue() {
        return new Queue(AI_request_queue, true);
    }
    // AI服务交换机
    @Bean
    public DirectExchange aiExchange() {
        return new DirectExchange(AI_exchange);
    }
    // 绑定队列与交换机路由
    @Bean
    public Binding bindingAi() {
        return BindingBuilder.bind(aiRequestQueue()).to(aiExchange()).with(AI_routing_key);
    }


    // 配置消息转换器
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    // 设置统计分析服务队列工厂
    @Bean(stats_container_factory)
    public SimpleRabbitListenerContainerFactory statsContainerFactory(ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 设置消息转换器（重要）
        factory.setMessageConverter(jsonMessageConverter());

        // 预取值，每个并发实例只能取一个
        factory.setPrefetchCount(1);

        // 并发实例数，设置为10-15个
        factory.setConcurrentConsumers(10);
        factory.setMaxConcurrentConsumers(15);

        // 设置自动确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        return factory;
    }

    // 设置工时填报提交服务队列工厂
    @Bean(hour_log_submit_container_factory)
    public SimpleRabbitListenerContainerFactory hourLogSubmitContainerFactory(ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 设置消息转换器（重要）
        factory.setMessageConverter(jsonMessageConverter());

        // 预取值，每个并发实例只能取一个
        factory.setPrefetchCount(1);

        // 并发实例数，设置为5-10个
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);

        // 设置自动确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        return factory;
    }

}
