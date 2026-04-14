package com.yixuan.yh.video.service;

import java.util.List;

public interface VideoTagService {
    List<String> getSimpleVideoTags();

    List<Long> getVideoTags(Long videoId);
}
