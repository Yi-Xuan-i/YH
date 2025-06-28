package com.yixuan.yh.ai.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("conversation_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {
    private Long messageId; // 消息主键
    private Long conversationId; // 关联对话ID
    private String role; // 消息角色
    private String content; // 消息内容
}
