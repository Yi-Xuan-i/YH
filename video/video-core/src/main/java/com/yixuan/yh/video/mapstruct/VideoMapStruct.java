package com.yixuan.yh.video.mapstruct;

import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithLike;
import com.yixuan.yh.video.pojo.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VideoMapStruct {

    VideoMapStruct INSTANCE = Mappers.getMapper(VideoMapStruct.class);

    GetUploadedVideoResponse toGetUploadedVideoResponse(Video video);
    GetPublishedVideoResponse toGetPublishedVideoResponse(Video video);
    GetProcessingVideoResponse toGetProcessingVideoResponse(Video video);
    GetRejectedVideoResponse toGetRejectedVideoResponse(Video video);
    GetLikeVideoResponse toGetLikeVideoResponse(VideoWithLike videoWithLike);
}
