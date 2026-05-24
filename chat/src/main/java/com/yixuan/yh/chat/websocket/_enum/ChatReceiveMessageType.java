package com.yixuan.yh.chat.websocket._enum;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChatReceiveMessageType {
    NORMAL(0, "普通消息"),
    ACK(1, "ACK消息");

    private final int code;
    private final String desc;

    @JsonValue
    private int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
