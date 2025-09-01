package com.yixuan.yh.video.feign;

import com.yixuan.yh.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "videoService", contextId = "VideoPrivateClient")
public interface VideoPrivateClient {

     @PutMapping("/video/api/private/to-published/{videoId}")
     Result<Void> putVideoStatusToPublished(@PathVariable Long videoId);
}