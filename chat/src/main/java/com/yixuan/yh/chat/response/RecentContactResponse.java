package com.yixuan.yh.chat.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentContactResponse {
    Long conversationId;
    Long contactId;
    String contactName;
    String contactAvatar;
    Integer unreadCount;
    LocalDateTime updateTime;
}
