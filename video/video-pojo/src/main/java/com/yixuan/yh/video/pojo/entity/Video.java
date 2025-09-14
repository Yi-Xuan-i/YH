package com.yixuan.yh.video.pojo.entity;

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
    VideoStatus status;
    LocalDateTime createdTime;

    public enum VideoStatus {
        UPLOADED,
        PROCESSING,
        REJECTED,
        PUBLISHED
    }

}
