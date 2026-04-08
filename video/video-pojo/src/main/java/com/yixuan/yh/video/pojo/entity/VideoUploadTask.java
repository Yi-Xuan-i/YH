package com.yixuan.yh.video.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@TableName("video_upload_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadTask {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    Long id;

    /**
     * 上传id（比如由初始化分片任务返回的id）
     */
    String uploadId;

    /**
     * 上传者id
     */
    private Long userId;

    /**
     * key
     */
    @TableField("`key`")
    private String key;

    /**
     * 总分块数
     */
    private Integer totalChunks;

    /**
     * 上传类型
     */
    private UploadType uploadType;

    /**
     * 0=上传中,1=已完成,2=已过期
     */
    private UploadStatus status;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    @Getter
    @AllArgsConstructor
    public enum UploadType {
        NORMAL(0, "普通上传"),
        MULTIPART(1, "分片上传");

        @EnumValue
        private final int code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum UploadStatus {
        UPLOADING(0, "上传中"),
        PROCESSING(1, "处理中"),
        COMPLETED(2, "已完成"),
        EXPIRED(3, "已过期");

        @EnumValue
        private final int code;
        private final String desc;
    }
}
