package com.yixuan.yh.video.feign;

import com.yixuan.yh.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "videoService", contextId = "VideoTagPrivateClient")
public interface VideoTagPrivateClient {
    @GetMapping("/video/api/private/tag/{videoId}")
    Result<List<Long>> getVideoTags(@PathVariable Long videoId);
}
