package com.yixuan.yh.video.pojo.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCommentIncrMessage {

    private List<CommentIncr> commentIncrList;
    private List<ReplyIncr> replyIncrList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentIncr {
        Long videoId;
        Integer incrNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyIncr {
        Long rootId;
        Integer incrNumber;
    }
}
