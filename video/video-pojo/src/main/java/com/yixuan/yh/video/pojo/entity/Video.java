package com.yixuan.yh.video.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("video")
@Data
public class Video {
    @TableId(type = IdType.ASSIGN_ID)
    Long id;
    Long creatorId;
    String url;
    String coverUrl;
    String description;
    Long likes;
    Long comments;
    Long favorites;
    VideoStatus status;
    @TableField(fill = FieldFill.INSERT)
    LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    LocalDateTime updatedAt;

    public enum VideoStatus {
        DRAFT(0, "草稿"),
        PENDING_REVIEW(1, "待审核"),
        REVIEWING(2, "审核中"),
        PROCESSING(3, "转码中"),
        REJECTED(4, "已拒绝"),
        PUBLISHED(5, "已发布");

        @EnumValue
        private final int code;

        private final String desc;

        VideoStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
