package com.yixuan.yh.video.pojo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadTask {
    /**
     * 上传id
     */
    private Long id;

    /**
     * 上传者id
     */
    private Long userId;

    /**
     * 文件总大小(字节)
     */
    private Long fileSize;

    /**
     * 总分块数
     */
    private Integer totalChunks;

    /**
     * 分块位图
     */
    private byte[] chunkBitmap;

    /**
     * 0=上传中,1=已完成,2=已过期
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
