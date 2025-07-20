package com.yixuan.yh.chat.inter;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class UserSubscriptionInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // 获取用户ID
            Long userId = (Long) accessor.getSessionAttributes().get("id");
            if (userId != null) {
                String originalDestination = accessor.getDestination();

                // 根据订阅路径决定是否添加用户ID
                if ("/queue/user".equals(originalDestination)) {
                    accessor.setDestination(originalDestination + "-" + userId);
                }
            }
        }

        return message;
    }
}