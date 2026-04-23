package com.yixuan.yh.video.pojo.request;

import com.yixuan.yh.video.pojo._enum.InteractionStatus;
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Record {
        Long userId;
        Long videoId;
        InteractionStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Incr {
        Long videoId;
        Integer incrNumber;
    }
}
