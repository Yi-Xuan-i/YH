package com.yixuan.yh.chat.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    Long id;
    Long conversationId;
    Long senderId;
    String content;
    ContentType contentType;
    LocalDateTime createdTime;

    public enum ContentType {
        TEXT,
        IMAGE
    }
}
