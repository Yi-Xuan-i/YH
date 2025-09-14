package com.yixuan.yh.video.pojo.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetPublishedVideoResponse {
    Long id;
    String url;
    String coverUrl;
    String description;
    Long likes;
    Long comments;
    Long favorites;
    LocalDateTime createdTime;
}
