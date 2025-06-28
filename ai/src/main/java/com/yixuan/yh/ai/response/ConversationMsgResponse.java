package com.yixuan.yh.ai.response;

import lombok.Data;

@Data
public class ConversationMsgResponse {
    private String role; // 消息角色
    private String content; // 消息内容
}
