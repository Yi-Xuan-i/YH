package com.yixuan.yh.video.pojo.request;

import lombok.Data;

@Data
public class PostCommentRequest {
    Long rootId;
    Long parentId;
    String content;
}
