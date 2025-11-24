package com.yixuan.yh.video.pojo.entity.multi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCollectionsWithVideo {
    private Long id;
    private Long collectionId;
    private Long videoId;
    private String coverUrl;
    private String url;
    private String description;
    private Long creatorId;
}
