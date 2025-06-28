package com.yixuan.yh.product.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory batchContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setBatchListener(true); // 启用批量监听
        factory.setConsumerBatchEnabled(true); // 消费者批量拉取
        factory.setBatchSize(10); // 每批最多处理10条
        factory.setReceiveTimeout(5000L); // 关键！超时时间5秒（即使未满batchSize）
        factory.setPrefetchCount(100); // 预取数量需足够大
        return factory;
    }
}