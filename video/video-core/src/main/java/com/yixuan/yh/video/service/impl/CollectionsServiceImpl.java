package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.mapper.VideoUserCollectionsMapper;
import com.yixuan.yh.video.mapstruct.CollectionsMapStruct;
import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.request.PutCollectionsRequest;
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
    public List<GetCollectionsResponse> getCollections(Long userId, Long lastMinId) {
        return videoUserCollectionsMapper.selectByUserId(userId, lastMinId)
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

    @Override
    public void putCollections(Long collectionsId, PutCollectionsRequest putCollectionsRequest) {
        // 转换格式
        VideoUserCollections videoUserCollections = CollectionsMapStruct.INSTANCE.toVideoUserCollections(putCollectionsRequest);
        videoUserCollections.setId(collectionsId);

        // 数据库操作
        videoUserCollectionsMapper.update(videoUserCollections, new LambdaUpdateWrapper<VideoUserCollections>()
                .eq(VideoUserCollections::getId, videoUserCollections.getId()));
    }

    @Override
    public void deleteCollections(Long collectionsId) {
        videoUserCollectionsMapper.deleteById(collectionsId);
    }
}
