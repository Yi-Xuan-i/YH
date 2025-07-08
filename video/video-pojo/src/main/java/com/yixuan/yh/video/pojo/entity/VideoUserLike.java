package com.yixuan.yh.video.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoUserLike {
    Long id;
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
