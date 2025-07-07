package com.yixuan.yh.video.mapstruct;

import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import com.yixuan.yh.video.pojo.request.VideoLikeBatchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InteractionMapStruct {

    InteractionMapStruct INSTANCE = Mappers.getMapper(InteractionMapStruct.class);

    VideoUserLike videoRecordToVideoUserLike(VideoLikeBatchRequest.LikeRecord likeRecord);
}
