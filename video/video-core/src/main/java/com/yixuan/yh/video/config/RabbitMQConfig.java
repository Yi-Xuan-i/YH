package com.yixuan.yh.video.config;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.mapper.MessageOutBoxMapper;
import com.yixuan.yh.video.pojo.entity.MessageOutbox;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter() {
            @Override
            protected Message createMessage(Object object, MessageProperties props) {
                props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return super.createMessage(object, props);
            }
        };
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory, MessageOutBoxMapper messageOutBoxMapper, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(messageConverter);

        // 设置强制执行退回回调
        rabbitTemplate.setMandatory(true);

        // Publish Confirm
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData == null) return;

            if (ack) {
                messageOutBoxMapper.update(new LambdaUpdateWrapper<MessageOutbox>()
                        .set(MessageOutbox::getStatus, MessageOutbox.OutboxStatus.SUCCESS)
                        .eq(MessageOutbox::getId, correlationData.getId()));
            }
        });

        // Publish Return
        rabbitTemplate.setReturnsCallback(returned -> {
        });

        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory batchContainerFactory(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setBatchListener(true); // 启用批量监听
        factory.setConsumerBatchEnabled(true); // 消费者批量拉取
        factory.setBatchSize(500); // 每批最多处理500条
        factory.setReceiveTimeout(5000L); // 关键！超时时间5秒（即使未满batchSize）
        factory.setPrefetchCount(500); // 预取数量需足够大
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }

    @Bean
    public Queue queue() { return new Queue(RabbitMQConstant.VIDEO_REVIEW_QUEUE, true); }
}