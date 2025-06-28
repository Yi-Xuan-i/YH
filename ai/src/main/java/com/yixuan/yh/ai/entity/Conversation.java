package com.yixuan.yh.ai.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("conversation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    private Long conversationId; // 对话主键
    private Long userId; // 用户标识
    private String title; // 对话标题
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 最后更新时间
}

