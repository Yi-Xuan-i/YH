package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.request.PostVideoRequest;
import com.yixuan.yh.video.pojo.response.VideoMainResponse;
import com.yixuan.yh.video.pojo.response.VideoMainWithInteractionResponse;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    List<VideoMainResponse> getVideos();

    List<VideoMainWithInteractionResponse> getVideosWithInteractionStatus(Long userId);

    void postVideo(Long userId, PostVideoRequest postVideoRequest) throws IOException, InterruptedException;
}
