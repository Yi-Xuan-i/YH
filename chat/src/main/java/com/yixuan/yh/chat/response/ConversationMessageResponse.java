package com.yixuan.yh.chat.response;

import com.yixuan.yh.chat.entity.ChatMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationMessageResponse {
    Long id;
    Long conversationId;
    Boolean isUser;
    String content;
    ChatMessage.ContentType contentType;
    LocalDateTime createdTime;
}
