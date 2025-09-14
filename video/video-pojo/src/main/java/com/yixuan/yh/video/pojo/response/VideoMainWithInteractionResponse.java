package com.yixuan.yh.video.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoMainWithInteractionResponse {
    Long Id;
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
