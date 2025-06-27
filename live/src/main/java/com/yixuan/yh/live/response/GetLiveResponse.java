package com.yixuan.yh.live.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetLiveResponse {
    Long roomId;
    Long anchorId;
    String anchorName;
    String title;
    String coverUrl;
    LocalDateTime createdTime;
}
