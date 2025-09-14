package com.yixuan.yh.video.pojo.request;

import lombok.Data;

@Data
public class PostCommentRequest {
    Long parentId;
    String content;
}
