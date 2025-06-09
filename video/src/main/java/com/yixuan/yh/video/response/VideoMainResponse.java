package com.yixuan.yh.video.response;

import lombok.Data;

@Data
public class VideoMainResponse {
    Long Id;
    Long creatorId;
    String creatorName;
    String creatorAvatar;
    String description;
    String url;
    Long likes;
    Long comments;
    Long favorites;
}
