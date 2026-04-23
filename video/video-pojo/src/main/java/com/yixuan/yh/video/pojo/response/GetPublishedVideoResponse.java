package com.yixuan.yh.video.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetPublishedVideoResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    Long id;
    String url;
    String coverUrl;
    String description;
    Long likes;
    Long comments;
    Long favorites;
    LocalDateTime createdTime;
}
