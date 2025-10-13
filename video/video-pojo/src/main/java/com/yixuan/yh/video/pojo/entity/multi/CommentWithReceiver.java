package com.yixuan.yh.video.pojo.entity.multi;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithReceiver {
    private Long id;
    private String content;
    private Long senderId;
    private Long receiverCommentId;
    private Long receiverId;
    private Long parentId;
    private Integer likeCount;
    private LocalDateTime updatedAt;
}
