package com.yixuan.yh.chat.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentContactResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long conversationId;
    private Long contactId;
    private String contactName;
    private String contactAvatar;
    private String lastMessage;
    private Integer unreadCount;
    private LocalDateTime updatedTime;
}
