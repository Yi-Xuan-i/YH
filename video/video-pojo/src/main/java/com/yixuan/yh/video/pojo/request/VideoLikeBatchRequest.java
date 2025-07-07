package com.yixuan.yh.video.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoLikeBatchRequest {

    List<LikeRecord> likeRecordList;
    List<LikeIncr> likeIncrList;

    @Data
    public static class LikeRecord {
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
    public static class LikeIncr {
        Long videoId;
        Long incrNumber;
    }
}
