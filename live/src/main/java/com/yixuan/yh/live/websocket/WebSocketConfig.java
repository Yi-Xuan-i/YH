package com.yixuan.yh.live.websocket;

import com.yixuan.yh.live.websocket.inter.HandshakeInterceptor;
import com.yixuan.yh.live.websocket.inter.SubscriptionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/me/ws") // WebSocket连接端点
                .addInterceptors(new HandshakeInterceptor())
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SubscriptionInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 使用RabbitMQ作为外部Broker
        registry.enableStompBrokerRelay("/topic") // broker处理
                .setRelayHost("rabbitmq")
                .setRelayPort(61613) // RabbitMQ STOMP端口
                .setSystemLogin("guest")
                .setSystemPasscode("guest")
                .setClientLogin("guest")
                .setClientPasscode("guest");

        registry.setApplicationDestinationPrefixes("/app"); // controller处理
    }
}