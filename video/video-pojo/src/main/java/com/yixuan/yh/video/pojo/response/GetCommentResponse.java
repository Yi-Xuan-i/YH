package com.yixuan.yh.video.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetCommentResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String content;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String name;
    private String avatarUrl;
    private Integer likeCount;
    private LocalDateTime updatedAt;
}
