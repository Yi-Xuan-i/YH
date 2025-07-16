package com.yixuan.yh.live.websocket;

import com.yixuan.yh.live.inter.HandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
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
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 使用RabbitMQ作为外部Broker
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("106.13.105.230")
                .setRelayPort(61613) // RabbitMQ STOMP端口
                .setSystemLogin("guest")
                .setSystemPasscode("guest")
                .setClientLogin("guest")
                .setClientPasscode("guest");

        registry.setApplicationDestinationPrefixes("/app");
    }
}