package com.yixuan.yh.live.websocket.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LiveMessage {
    private MessageType type;
    private Object content;

    public enum MessageType {
        CHAT,
        PRODUCT,
        EXPLAIN;
    }
}
