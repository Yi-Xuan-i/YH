package com.yixuan.yh.video.service;

import com.yixuan.yh.video.request.PostVideoRequest;
import com.yixuan.yh.video.response.VideoMainResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    List<VideoMainResponse> getVideos();

    void postVideo(Long userId, PostVideoRequest postVideoRequest) throws IOException, InterruptedException;
}
