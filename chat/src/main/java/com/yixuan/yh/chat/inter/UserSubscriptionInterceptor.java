package com.yixuan.yh.chat.inter;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserSubscriptionInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Long userId = (Long) accessor.getSessionAttributes().get("id");
            if (userId != null) {
                String originalDestination = accessor.getDestination();
                if (originalDestination != null && originalDestination.startsWith("/queue/user")) {
                    // 修改目标路径
                    String newDestination = originalDestination + "." + userId;
                    accessor.setDestination(newDestination);

                    // 创建包含新头信息的新消息
                    return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                }
            }
        }
        return message;
    }
}