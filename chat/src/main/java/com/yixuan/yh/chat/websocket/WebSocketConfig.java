package com.yixuan.yh.chat.websocket;

import com.yixuan.yh.chat.websocket.inter.StompHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/me/ws") // WebSocket连接端点
                .setHandshakeHandler(new StompHandshakeHandler())
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay( "/topic", "/queue") // 以这为前缀的直接发送给消息代理（不经过“controller”）
                .setRelayHost("106.13.105.230")
                .setRelayPort(61613) // RabbitMQ STOMP端口
                .setSystemLogin("guest")
                .setSystemPasscode("guest")
                .setClientLogin("guest")
                .setClientPasscode("guest");

        registry.setUserDestinationPrefix("/user"); // 订阅用户前缀
        registry.setApplicationDestinationPrefixes("/app"); // controller 前缀
    }
}