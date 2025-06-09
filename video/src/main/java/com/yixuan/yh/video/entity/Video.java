package com.yixuan.yh.video.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Video {
    Long id;
    Long creatorId;
    String url;
    String coverUrl;
    String description;
    Long likes;
    Long comments;
    Long favorites;
    LocalDateTime createdTime;
}
