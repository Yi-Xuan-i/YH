package com.yixuan.yh.video.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoTag {
    Long id;
    String name;
    LocalDateTime createdTime;
}
