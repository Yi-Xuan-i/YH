package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.response.*;
import com.yixuan.yh.video.pojo.request.PostVideoMessageRequest;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface VideoService {
    List<VideoMainResponse> getVideos();

    List<VideoMainWithInteractionResponse> getVideosWithInteractionStatus(Long userId);

    String postVideoStart(Long userId, Long fileSize, Integer totalChunks);

    void postVideo(Long userId, Long uploadId, Long partNumber, MultipartFile file) throws Exception;

    void postVideoEnd(Long uploadId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void postVideoMessage(Long userId, PostVideoMessageRequest postVideoMessageRequest) throws IOException, InterruptedException;

    void putVideoStatusToPublished(Long videoId);

    List<GetUploadedVideoResponse> getUploadedVideo(Long userId);

    List<GetPublishedVideoResponse> getPublishedVideo(Long userId);

    List<GetProcessingVideoResponse> getProcessingVideo(Long userId);

    List<GetRejectedVideoResponse> getRejectedVideo(Long userId);
}
