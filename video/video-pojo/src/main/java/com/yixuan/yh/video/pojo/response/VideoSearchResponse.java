package com.yixuan.yh.video.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoSearchResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long creatorId;
    private String creatorName;
    private String description;
    private String coverUrl;
    private Long likes;
}
