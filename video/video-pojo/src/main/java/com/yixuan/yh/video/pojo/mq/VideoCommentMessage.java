package com.yixuan.yh.video.pojo.mq;

import lombok.Data;

@Data
public class VideoCommentMessage {
    Long id;
    Long parentId;
    Long videoId;
    Long userId;
    String content;
}