package com.yixuan.yh.video.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoTag {
    Long id;
    String name;
    LocalDateTime createdTime;
}
