package com.yixuan.yh.video.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("video_user_collections_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoUserCollectionsItem {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long collectionsId;
    private Long userId;
    private Long videoId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
