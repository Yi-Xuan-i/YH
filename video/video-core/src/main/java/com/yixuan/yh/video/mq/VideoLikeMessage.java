package com.yixuan.yh.video.mq;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
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

        Status(int value) {
        }
    }

}
