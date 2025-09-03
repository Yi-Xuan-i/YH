package com.yixuan.yh.video.pojo.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetCommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private Integer likeCount;
    private LocalDateTime updatedAt;
}
