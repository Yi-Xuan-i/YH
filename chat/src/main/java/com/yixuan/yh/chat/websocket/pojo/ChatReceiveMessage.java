package com.yixuan.yh.chat.websocket.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatReceiveMessage {
    Long senderId;
    String content;
    LocalDateTime sentTime;
}
