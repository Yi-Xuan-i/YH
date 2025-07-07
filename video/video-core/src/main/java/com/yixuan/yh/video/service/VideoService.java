package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.request.PostVideoRequest;
import com.yixuan.yh.video.pojo.response.VideoMainResponse;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    List<VideoMainResponse> getVideos();

    void postVideo(Long userId, PostVideoRequest postVideoRequest) throws IOException, InterruptedException;
}
