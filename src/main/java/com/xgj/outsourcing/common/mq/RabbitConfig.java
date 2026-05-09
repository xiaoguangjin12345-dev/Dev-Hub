package com.xgj.outsourcing.common.mq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
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
    public static final String AI_service = "sys.ai_service.queue";

    public static final String AI_container_factory = "aiContainerFactory";
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
    public Queue AIServiceQueue(){
        return new Queue(AI_service);
    }

    // 配置消息转换器
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 设置AI服务队列工厂
    @Bean(AI_container_factory)
    public SimpleRabbitListenerContainerFactory aiContainerFactory(ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 预取值，每个并发实例只能取一个
        factory.setPrefetchCount(1);

        // 并发实例数，设置为5-10个
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);

        // 设置自动确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        return factory;
    }

    // 设置统计分析服务队列工厂
    @Bean(stats_container_factory)
    public SimpleRabbitListenerContainerFactory statsContainerFactory(ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

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
