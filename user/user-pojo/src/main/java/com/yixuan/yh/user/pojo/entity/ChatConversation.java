package com.yixuan.yh.user.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("chat_conversation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatConversation {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private Integer user1UnreadCount;
    private Integer user2UnreadCount;
    private LocalDateTime updatedTime;
    private LocalDateTime createdTime;
}
