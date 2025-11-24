package com.yixuan.yh.video.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class GetFavoriteVideoResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long favoriteId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long creatorId;
    private String creatorName;
    private String url;
    private String coverUrl;
    private String description;
    private Integer favorites;
}
