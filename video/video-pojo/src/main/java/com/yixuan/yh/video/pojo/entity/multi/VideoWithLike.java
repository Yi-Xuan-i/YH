package com.yixuan.yh.video.pojo.entity.multi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoWithLike {

    private Long likeId;

    private Long videoId;

    private Long creatorId;

    private String url;

    private String coverUrl;

    private String description;

    private Integer likes;
}