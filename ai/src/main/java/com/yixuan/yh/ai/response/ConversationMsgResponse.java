package com.yixuan.yh.ai.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ConversationMsgResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId; // 消息主键
    private String role; // 消息角色
    private String content; // 消息内容
}
