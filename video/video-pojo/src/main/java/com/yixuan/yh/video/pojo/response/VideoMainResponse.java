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
    Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    Long creatorId;
    String creatorName;
    String creatorAvatar;
    String description;
    String url;
    Long likes;
    Long comments;
    Long favorites;
    Boolean isFollowed;
    Boolean isLike;
    Boolean isFavorite;
}