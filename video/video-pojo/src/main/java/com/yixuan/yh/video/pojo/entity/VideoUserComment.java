package com.yixuan.yh.video.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoUserComment {
    /**
     * 评论唯一ID
     */
    private Long id;

    /**
     * 关联视频ID
     */
    private Long videoId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者用户ID
     */
    private Long userId;

    /**
     * 直接父评论ID
     */
    private Long parentId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
