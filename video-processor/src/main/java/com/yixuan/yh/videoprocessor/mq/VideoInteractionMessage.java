package com.yixuan.yh.videoprocessor.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoInteractionMessage {

    Long userId;
    Long videoId;
    Status status;

    public enum Status {
        FRONT(1),
        BACK(-1);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
