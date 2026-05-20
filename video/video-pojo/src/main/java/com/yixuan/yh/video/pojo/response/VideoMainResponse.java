package com.yixuan.yh.video.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoMainResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long creatorId;
    private String creatorName;
    private String creatorAvatar;
    private String description;
    private String url;
    private Long likes;
    private Long comments;
    private Long favorites;
    private Boolean isFollowed;
    private Boolean isLike;
    private Boolean isFavorite;
}