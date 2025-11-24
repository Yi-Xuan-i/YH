package com.yixuan.yh.video.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("video_user_collections_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoUserCollectionsItem {
    private Long id;
    private Long collectionsId;
    private Long videoId;
    private Long createdAt;
}
