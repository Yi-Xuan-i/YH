package com.yixuan.yh.chat.entity.multi;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentContact {
    Long conversationId;
    Long contactId;
    Integer unreadCount;
    LocalDateTime updateTime;
}
