package com.yixuan.yh.video.mapstruct;

import com.yixuan.yh.video.pojo.entity.VideoUserComment;
import com.yixuan.yh.video.pojo.request.PostCommentRequest;
import com.yixuan.yh.video.pojo.response.GetCommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InteractionMapStruct {

    InteractionMapStruct INSTANCE = Mappers.getMapper(InteractionMapStruct.class);

    VideoUserComment toVideoUserComment(PostCommentRequest postCommentRequest);

    GetCommentResponse toGetCommentResponse(VideoUserComment videoUserComment);
}
