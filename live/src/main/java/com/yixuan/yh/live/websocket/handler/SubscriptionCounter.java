package com.yixuan.yh.live.websocket.handler;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubscriptionCounter {

    private final RedissonClient redissonClient;

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes != null) {
            Long userId = (Long) sessionAttributes.get("userId");
            Long roomId = (Long) sessionAttributes.get("roomId");
            RSet<Long> rSet = redissonClient.getSet("live:online:" + roomId);
            rSet.add(userId);
        }
    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes != null) {
            Long userId = (Long) sessionAttributes.get("userId");
            Long roomId = (Long) sessionAttributes.get("roomId");
            RSet<Long> rSet = redissonClient.getSet("live:online:" + roomId);
            rSet.remove(userId);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes != null) {
            Long userId = (Long) sessionAttributes.get("userId");
            Long roomId = (Long) sessionAttributes.get("roomId");
            RSet<Long> rSet = redissonClient.getSet("live:online:" + roomId);
            rSet.remove(userId);
        }
    }
}