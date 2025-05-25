package com.yixuan.yh.video.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Video {
    Long id;
    String url;
    String coverUrl;
    String description;
    LocalDateTime createdTime;
}
