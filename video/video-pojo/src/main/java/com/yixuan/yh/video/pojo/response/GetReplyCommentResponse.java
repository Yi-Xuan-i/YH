package com.yixuan.yh.video.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetReplyCommentResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String content;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverCommentId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverId;
    private String receiverName;
    private String receiverAvatar;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    private Integer likeCount;
    private LocalDateTime updatedAt;
}
