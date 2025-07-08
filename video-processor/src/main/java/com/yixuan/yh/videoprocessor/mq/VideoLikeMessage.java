package com.yixuan.yh.videoprocessor.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoLikeMessage {

    Long userId;
    Long videoId;
    Status status;

    public enum Status {
        LIKE(1),
        UNLIKE(-1);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
