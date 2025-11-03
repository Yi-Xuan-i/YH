package com.yixuan.yh.video.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("video_user_collections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoUserCollections {
    private Long id;
    private Long userId;
    private String name;
    private Integer itemCount;
    private Integer isPublic; // 0-私密、1-公开
    private LocalDateTime updateAt;
    private LocalDateTime createdAt;
}