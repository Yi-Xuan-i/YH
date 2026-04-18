package com.yixuan.yh.live.websocket.pojo;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LiveMessage {
    private MessageType type;
    private Object content;

    public enum MessageType {
        CHAT(0),
        PRODUCT(1),
        EXPLAIN(2);

        private final int code;

        MessageType(int code) {
            this.code = code;
        }

        @JsonValue
        public int getCode() {
            return code;
        }
    }
}
