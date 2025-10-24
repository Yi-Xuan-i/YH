package com.yixuan.yh.video.mapstruct;

import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.response.GetCollectionsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CollectionsMapStruct {
    CollectionsMapStruct INSTANCE = Mappers.getMapper(CollectionsMapStruct.class);

    GetCollectionsResponse toGetCollectionsResponse(VideoUserCollections videoUserCollections);

    VideoUserCollections toVideoUserCollections(PostCollectionsRequest postCollectionsRequest);
}
