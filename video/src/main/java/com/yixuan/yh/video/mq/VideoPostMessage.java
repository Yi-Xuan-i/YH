package com.yixuan.yh.video.mq;

import lombok.Data;

import java.util.List;

@Data
public class VideoPostMessage {
    Long videoId;
    String videoUrl;
    String description;
    List<String> tagNameList;
}
