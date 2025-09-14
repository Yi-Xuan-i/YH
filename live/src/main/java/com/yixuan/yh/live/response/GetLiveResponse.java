package com.yixuan.yh.live.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetLiveResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    Long roomId;
    Long anchorId;
    String anchorName;
    String title;
    String coverUrl;
    LocalDateTime createdTime;
}
