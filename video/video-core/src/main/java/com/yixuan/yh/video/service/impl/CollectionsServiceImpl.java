package com.yixuan.yh.video.service.impl;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.mapper.VideoUserCollectionsMapper;
import com.yixuan.yh.video.mapstruct.CollectionsMapStruct;
import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.response.GetCollectionsResponse;
import com.yixuan.yh.video.service.CollectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionsServiceImpl implements CollectionsService {

    private final VideoUserCollectionsMapper videoUserCollectionsMapper;
    private final SnowflakeUtils snowflakeUtils;

    @Override
    public List<GetCollectionsResponse> getCollections(Long userId) {
        return videoUserCollectionsMapper.selectByUserId(userId)
                .stream().map(CollectionsMapStruct.INSTANCE::toGetCollectionsResponse)
                .toList();
    }

    @Override
    public String postCollections(Long userId, PostCollectionsRequest postCollectionsRequest) {
        VideoUserCollections videoUserCollections = CollectionsMapStruct.INSTANCE.toVideoUserCollections(postCollectionsRequest);
        videoUserCollections.setId(snowflakeUtils.nextId());
        videoUserCollections.setUserId(userId);
        videoUserCollections.setUpdateAt(LocalDateTime.now());
        videoUserCollections.setCreatedAt(LocalDateTime.now());

        videoUserCollectionsMapper.insert(videoUserCollections);

        return videoUserCollections.getId().toString();
    }
}
