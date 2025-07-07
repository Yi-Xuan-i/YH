package com.yixuan.yh.video.feign;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.video.pojo.request.VideoLikeBatchRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "videoService", contextId = "VideoPrivateClient")
public interface VideoPrivateClient {
     @PostMapping("/video/api/private/interaction/like-batch")
     Result<Void> likeBatch(@RequestBody VideoLikeBatchRequest videoLikeBatchRequest);
}