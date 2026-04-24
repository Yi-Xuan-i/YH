package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.AWSUtils;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionsServiceImpl extends ServiceImpl<VideoUserCollectionsMapper, VideoUserCollections> implements CollectionsService {

    private final VideoUserCollectionsMapper videoUserCollectionsMapper;
    private final SnowflakeUtils snowflakeUtils;
    private final VideoUserCollectionsItemMapper videoUserCollectionsItemMapper;
    private final UserPrivateClient userPrivateClient;
    private final AWSUtils awsUtils;

    @Override
    public List<GetCollectionsResponse> getCollections(Long userId, Long lastMinId) {
        return videoUserCollectionsMapper.selectByUserId(userId, lastMinId)
                .stream().map(CollectionsMapStruct.INSTANCE::toGetCollectionsResponse)
                .toList();
    }

    @Override
    public List<GetCollectionsItemResponse> getCollectionsItemList(Long userId, Long collectionsId, Long lastMinId) {
        // 查询收藏夹所属用户
        Long ownerId = videoUserCollectionsMapper.selectUserIdById(collectionsId);

        // 判断收藏夹是否存在
        if (ownerId == null) {
            throw new YHClientException("该收藏夹不存在！");
        }

        // 鉴权（当前收藏夹是否属于当前用户）
        if (!ownerId.equals(userId)) {
            throw new YHClientException("你没有权限！");
        }

        // 查询数据
        List<VideoCollectionsWithVideo> videoCollectionsWithVideoList = videoUserCollectionsItemMapper.selectCollectionsItemList(collectionsId, lastMinId);
        if (videoCollectionsWithVideoList.isEmpty()) {
            return Collections.emptyList();
        }

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
                .map(v -> {
                    GetCollectionsItemResponse response = CollectionsMapStruct.INSTANCE.toGetCollectionsItemResponse(v);
                    response.setCreatorName(creatorIdToNameMap.get(response.getCreatorId()));
                    response.setUrl(awsUtils.generateAccessUrl(v.getUrl()));
                    response.setCoverUrl(awsUtils.generateAccessUrl(v.getCoverUrl()));
                    return response;
                })
                .toList();
    }

    @Override
    public String postCollections(Long userId, PostCollectionsRequest postCollectionsRequest) {
        VideoUserCollections videoUserCollections = CollectionsMapStruct.INSTANCE.toVideoUserCollections(postCollectionsRequest);
        videoUserCollections.setId(snowflakeUtils.nextId());
        videoUserCollections.setUserId(userId);
        videoUserCollections.setUpdatedAt(LocalDateTime.now());
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
    public void deleteCollections(Long userId, Long collectionsId) {
        // 获取所需数据
        VideoUserCollections collections = videoUserCollectionsMapper.selectOne(new LambdaQueryWrapper<VideoUserCollections>()
                .select(VideoUserCollections::getUserId, VideoUserCollections::getName)
                .eq(VideoUserCollections::getId, collectionsId));
        // 判断收藏夹是否存在
        if (collections == null) {
            throw new YHClientException("该收藏夹不存在！");
        }
        // 鉴权（当前收藏夹是否属于当前用户）
        if (!collections.getUserId().equals(userId)) {
            throw new YHClientException("你没有权限！");
        }
        // 默认收藏夹不允许删除
        if ("默认收藏夹".equals(collections.getName())) {
            throw new YHClientException("默认收藏夹不允许删除！");
        }
        // 删除收藏夹
        videoUserCollectionsMapper.deleteById(collectionsId);
    }

    @Override
    @Transactional
    public Map<Long, Long> getDefaultCollectionsIdBatch(List<Long> list) {
        List<VideoUserCollections> videoUserCollectionsList = videoUserCollectionsMapper.selectList(new LambdaQueryWrapper<VideoUserCollections>()
                .select(VideoUserCollections::getId, VideoUserCollections::getUserId)
                .eq(VideoUserCollections::getName, "默认收藏夹")
                .in(VideoUserCollections::getUserId, list));
        // 如果没有默认收藏夹，则创建。
        List<Long> userIdWithDefaultCollections = videoUserCollectionsList.stream().map(VideoUserCollections::getUserId).toList();
        List<Long> userIdWithoutDefaultCollections = list.stream().filter(userId -> !userIdWithDefaultCollections.contains(userId)).toList();
        List<VideoUserCollections> videoUserCollectionsToInsert = userIdWithoutDefaultCollections.stream().map(userId -> {
            VideoUserCollections videoUserCollections = new VideoUserCollections();
            videoUserCollections.setUserId(userId);
            videoUserCollections.setName("默认收藏夹");
            return videoUserCollections;
        }).toList();
        if (!videoUserCollectionsToInsert.isEmpty()) {
            saveBatch(videoUserCollectionsToInsert);
            videoUserCollectionsList.addAll(videoUserCollectionsToInsert);
        }

        // 转换格式
        return videoUserCollectionsList.stream().collect(Collectors.toMap(VideoUserCollections::getUserId, VideoUserCollections::getId));
    }
}
