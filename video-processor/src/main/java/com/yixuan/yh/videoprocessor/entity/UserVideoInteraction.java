package com.yixuan.yh.videoprocessor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@TableName("user_video_interaction")
public class UserVideoInteraction {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 交互类型
     */
    private InteractionType type;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Getter
    @AllArgsConstructor
    public enum InteractionType {
        LIKE(1, "点赞"),
        COLLECT(2, "收藏"),
        COMMENT(3, "评论");

        @EnumValue
        private final int code;
        private final String desc;
    }
}