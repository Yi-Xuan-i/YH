package com.yixuan.yh.video.config;

import com.yixuan.yh.video.constant.RabbitMQConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue videoPostQueue(RabbitAdmin rabbitAdmin) {
        Queue videoPostQueue = new Queue(RabbitMQConstant.VIDEO_POST_QUEUE, true);
        rabbitAdmin.declareQueue(videoPostQueue);
        return videoPostQueue;
    }

}