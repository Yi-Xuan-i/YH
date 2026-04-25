package com.yixuan.yh.video.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCollectionsItemResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long collectionsId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId;
    private String coverUrl;
    private String url;
    private String description;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long creatorId;
    private String creatorName;
}