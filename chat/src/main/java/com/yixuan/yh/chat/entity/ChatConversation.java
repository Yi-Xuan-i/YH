package com.yixuan.yh.chat.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatConversation {
    Long id;
    Long user1Id;
    Long user2Id;
    Integer user1UnreadCount;
    Integer user2UnreadCount;
    LocalDateTime updatedTime;
    LocalDateTime createdTime;
}
