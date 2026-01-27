package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.feign.UserPrivateClient;
import com.yixuan.yh.video.mapper.VideoUserCollectionsItemMapper;
import com.yixuan.yh.video.mapper.VideoUserCollectionsMapper;
import com.yixuan.yh.video.mapstruct.CollectionsMapStruct;
import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import com.yixuan.yh.video.pojo.entity.multi.VideoCollectionsWithVideo;
import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.request.PutCollectionsRequest;
import com.yixuan.yh.video.pojo.response.GetCollectionsItemResponse;
import com.yixuan.yh.video.pojo.response.GetCollectionsResponse;
import com.yixuan.yh.video.service.CollectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CollectionsServiceImpl implements CollectionsService {

    private final VideoUserCollectionsMapper videoUserCollectionsMapper;
    private final SnowflakeUtils snowflakeUtils;
    private final VideoUserCollectionsItemMapper videoUserCollectionsItemMapper;
    private final UserPrivateClient userPrivateClient;

    @Override
    public List<GetCollectionsResponse> getCollections(Long userId, Long lastMinId) {
        return videoUserCollectionsMapper.selectByUserId(userId, lastMinId)
                .stream().map(CollectionsMapStruct.INSTANCE::toGetCollectionsResponse)
                .toList();
    }

    @Override
    public List<GetCollectionsItemResponse> getCollectionsItemList(Long userId, Long collectionsId) {
        // 查询收藏夹所属用户
        Long ownerId = videoUserCollectionsMapper.selectUserIdById(collectionsId);

        // 判断收藏夹是否存在
        if (ownerId == null) {
            throw new YHClientException("该收藏夹不存在！");
        }

        // 鉴权（当前收藏夹是否属于当前用户）
        if (!videoUserCollectionsMapper.selectUserIdById(collectionsId).equals(userId)) {
            throw new YHClientException("你没有权限！");
        }

        // 查询数据
        List<VideoCollectionsWithVideo> videoCollectionsWithVideoList = videoUserCollectionsItemMapper.selectCollectionsItemList(collectionsId);

        // 查询补充数据
        Result<Map<Long, String>> result = userPrivateClient.getNameBatch(videoCollectionsWithVideoList.
                stream()
                .map(VideoCollectionsWithVideo::getCreatorId)
                .toList()
        );

        Map<Long, String> creatorIdToNameMap = result.getData();

        // 转换格式
        return videoCollectionsWithVideoList
                .stream()
                .map(CollectionsMapStruct.INSTANCE::toGetCollectionsItemResponse)
                .peek(getCollectionsItemResponse -> getCollectionsItemResponse.setCreatorName(creatorIdToNameMap.get(getCollectionsItemResponse.getCreatorId())))
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
