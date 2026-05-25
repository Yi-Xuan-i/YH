package com.yixuan.yh.chat.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    Long id;
    Long conversationId;
    Long senderId;
    String content;
    ContentType contentType;
    LocalDateTime createdTime;

    @AllArgsConstructor
    public enum ContentType {
        TEXT(0, "普通文本"),
        IMAGE(1, "图片"),
        VIDEO(2, "视频");

        @EnumValue
        private final int code;
        private final String desc;

        @JsonValue
        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
