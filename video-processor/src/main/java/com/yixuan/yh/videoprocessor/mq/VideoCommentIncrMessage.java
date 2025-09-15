package com.yixuan.yh.videoprocessor.mq;

import lombok.Data;

@Data
public class VideoCommentIncrMessage {
    Long videoId;
    Long incrNumber;
}
