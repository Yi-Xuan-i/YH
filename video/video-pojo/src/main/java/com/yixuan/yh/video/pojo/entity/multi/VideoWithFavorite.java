package com.yixuan.yh.video.pojo.entity.multi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoWithFavorite {

    private Long favoriteId;

    private Long videoId;

    private Long creatorId;

    private String url;

    private String coverUrl;

    private String description;

    private Integer favorites;
}
