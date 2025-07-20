package com.yixuan.yh.chat.websocket.pojo;

import lombok.Data;

@Data
public class ChatSendMessage {
    Long conversationId;
    String content;
}
