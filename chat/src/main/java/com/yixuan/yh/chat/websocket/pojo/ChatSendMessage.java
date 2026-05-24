package com.yixuan.yh.chat.websocket.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSendMessage {
    private Long conversationId;
    private String clientMsgId;
    private String content;
}
