package com.yixuan.yh.video.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoInteractionBatchRequest {

    List<Record> interactionRecordList;
    List<Incr> interactionIncrList;

    @Data
    public static class Record {
        Long userId;
        Long videoId;
        Status status;

        public enum Status {
            LIKE(1),
            UNLIKE(-1);

            Status(int i) {
            }
        }
    }

    @Data
    public static class Incr {
        Long videoId;
        Long incrNumber;
    }
}
