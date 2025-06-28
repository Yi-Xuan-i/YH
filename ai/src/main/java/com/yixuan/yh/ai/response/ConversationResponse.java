package com.yixuan.yh.ai.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationResponse {
    private Long conversationId; // 对话主键
    private String title; // 对话标题
    private LocalDateTime updatedAt; // 最后更新时间
}
