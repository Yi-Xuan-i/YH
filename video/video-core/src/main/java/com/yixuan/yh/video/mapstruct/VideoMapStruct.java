package com.yixuan.yh.video.mapstruct;

import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.response.GetProcessingVideoResponse;
import com.yixuan.yh.video.pojo.response.GetPublishedVideoResponse;
import com.yixuan.yh.video.pojo.response.GetRejectedVideoResponse;
import com.yixuan.yh.video.pojo.response.GetUploadedVideoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VideoMapStruct {

    VideoMapStruct INSTANCE = Mappers.getMapper(VideoMapStruct.class);

    GetUploadedVideoResponse toGetUploadedVideoResponse(Video video);
    GetPublishedVideoResponse toGetPublishedVideoResponse(Video video);
    GetProcessingVideoResponse toGetProcessingVideoResponse(Video video);
    GetRejectedVideoResponse toGetRejectedVideoResponse(Video video);
}
