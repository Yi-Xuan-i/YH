package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.feign.UserPrivateClient;
import com.yixuan.yh.video.cache.VideoUserFavoriteCache;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.mapper.VideoUserCollectionsItemMapper;
import com.yixuan.yh.video.mapper.VideoUserCollectionsMapper;
import com.yixuan.yh.video.mapstruct.CollectionsMapStruct;
import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import com.yixuan.yh.video.pojo.entity.VideoUserCollectionsItem;
import com.yixuan.yh.video.pojo.entity.multi.VideoCollectionsWithVideo;
import com.yixuan.yh.video.pojo.request.DeleteCollectionsItemRequest;
import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.request.PutCollectionsRequest;
import com.yixuan.yh.video.pojo.request.TransferCollectionsItemRequest;
import com.yixuan.yh.video.pojo.response.GetCollectionsItemResponse;
import com.yixuan.yh.video.pojo.response.GetCollectionsResponse;
import com.yixuan.yh.video.service.CollectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionsServiceImpl extends ServiceImpl<VideoUserCollectionsMapper, VideoUserCollections> implements CollectionsService {

    private final VideoUserCollectionsMapper videoUserCollectionsMapper;
    private final SnowflakeUtils snowflakeUtils;
    private final VideoUserCollectionsItemMapper videoUserCollectionsItemMapper;
    private final UserPrivateClient userPrivateClient;
    private final AWSUtils awsUtils;
    private final VideoMapper videoMapper;
    private final VideoUserFavoriteCache videoUserFavoriteCache;

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
    public void putCollections(Long userId, Long collectionsId, PutCollectionsRequest putCollectionsRequest) {
        // 获取所需数据
        VideoUserCollections collections = videoUserCollectionsMapper.selectOne(new LambdaQueryWrapper<VideoUserCollections>()
                .select(VideoUserCollections::getUserId)
                .eq(VideoUserCollections::getId, collectionsId));

        // 判断收藏夹是否存在
        if (collections == null) {
            throw new YHClientException("该收藏夹不存在！");
        }

        // 鉴权（当前收藏夹是否属于当前用户）
        if (!collections.getUserId().equals(userId)) {
            throw new YHClientException("你没有权限！");
        }

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

    @Override
    @Transactional
    public void deleteCollectionsItemBatch(Long userId, DeleteCollectionsItemRequest deleteCollectionsItemRequest) {
        // 查询数据
        List<VideoUserCollectionsItem> videoUserCollectionsItemList = videoUserCollectionsItemMapper.selectBatchIds(deleteCollectionsItemRequest.getIds());

        // 是否为空
        if (videoUserCollectionsItemList.isEmpty()) {
            return;
        }

        // 鉴权（当前收藏夹项是否属于当前用户）
        for (VideoUserCollectionsItem v : videoUserCollectionsItemList) {
            if (!v.getUserId().equals(userId)) {
                throw new YHClientException("你没有权限！");
            }
        }

        // 删除收藏夹与视频关联
        videoUserCollectionsItemMapper.deleteBatchIds(deleteCollectionsItemRequest.getIds());

        // 更新收藏夹项数（下面的逻辑都待优化）
        Map<Long, Long> collectionsIdToCountMap = videoUserCollectionsItemList.stream()
                .collect(Collectors.groupingBy(VideoUserCollectionsItem::getCollectionsId, Collectors.counting()));
        collectionsIdToCountMap.forEach((collectionsId, count) ->
                videoUserCollectionsMapper.update(null, new LambdaUpdateWrapper<VideoUserCollections>()
                        .setSql("item_count = item_count - " + count)
                        .eq(VideoUserCollections::getId, collectionsId))
        );

        // 更新对应视频收藏数
        Map<Long, Long> videoIdToCountMap = videoUserCollectionsItemList.stream()
                .collect(Collectors.groupingBy(VideoUserCollectionsItem::getVideoId, Collectors.counting()));
        videoIdToCountMap.forEach((videoId, count) ->
                videoMapper.update(null, new LambdaUpdateWrapper<Video>()
                        .setSql("favorites = favorites - " + count)
                        .eq(Video::getId, videoId))
        );

        // 更新收藏关系缓存
        for (VideoUserCollectionsItem item : videoUserCollectionsItemList) {
            videoUserFavoriteCache.tryUnFavorite(userId, item.getVideoId());
        }
    }

    @Override
    @Transactional
    public void moveCollectionsItemBatch(Long userId, TransferCollectionsItemRequest transferCollectionsItemRequest) {
        List<VideoUserCollectionsItem> videoUserCollectionsItemList = getCollectionsItemsForTransfer(userId, transferCollectionsItemRequest);
        if (videoUserCollectionsItemList.isEmpty()) {
            return;
        }

        Long targetCollectionsId = transferCollectionsItemRequest.getTargetCollectionsId();
        checkCollectionsOwner(userId, targetCollectionsId);

        Set<Long> targetVideoIdSet = getTargetVideoIdSet(userId, targetCollectionsId, videoUserCollectionsItemList);
        Set<Long> movingVideoIdSet = new HashSet<>(targetVideoIdSet);
        List<VideoUserCollectionsItem> itemListToMove = new ArrayList<>();
        for (VideoUserCollectionsItem item : videoUserCollectionsItemList) {
            if (item.getCollectionsId().equals(targetCollectionsId) || !movingVideoIdSet.add(item.getVideoId())) {
                continue;
            }
            itemListToMove.add(item);
        }
        if (itemListToMove.isEmpty()) {
            return;
        }

        Map<Long, Long> sourceCollectionsIdToCountMap = itemListToMove.stream()
                .collect(Collectors.groupingBy(VideoUserCollectionsItem::getCollectionsId, Collectors.counting()));
        sourceCollectionsIdToCountMap.forEach((collectionsId, count) ->
                videoUserCollectionsMapper.update(null, new LambdaUpdateWrapper<VideoUserCollections>()
                        .setSql("item_count = item_count - " + count)
                        .eq(VideoUserCollections::getId, collectionsId))
        );
        videoUserCollectionsMapper.update(null, new LambdaUpdateWrapper<VideoUserCollections>()
                .setSql("item_count = item_count + " + itemListToMove.size())
                .eq(VideoUserCollections::getId, targetCollectionsId));

        videoUserCollectionsItemMapper.update(null, new LambdaUpdateWrapper<VideoUserCollectionsItem>()
                .set(VideoUserCollectionsItem::getCollectionsId, targetCollectionsId)
                .in(VideoUserCollectionsItem::getId, itemListToMove.stream().map(VideoUserCollectionsItem::getId).toList()));
    }

    @Override
    @Transactional
    public void copyCollectionsItemBatch(Long userId, TransferCollectionsItemRequest transferCollectionsItemRequest) {
        List<VideoUserCollectionsItem> videoUserCollectionsItemList = getCollectionsItemsForTransfer(userId, transferCollectionsItemRequest);
        if (videoUserCollectionsItemList.isEmpty()) {
            return;
        }

        Long targetCollectionsId = transferCollectionsItemRequest.getTargetCollectionsId();
        checkCollectionsOwner(userId, targetCollectionsId);

        Set<Long> targetVideoIdSet = getTargetVideoIdSet(userId, targetCollectionsId, videoUserCollectionsItemList);
        Set<Long> copiedVideoIdSet = new HashSet<>();
        List<VideoUserCollectionsItem> itemListToCopy = new ArrayList<>();
        for (VideoUserCollectionsItem item : videoUserCollectionsItemList) {
            if (targetVideoIdSet.contains(item.getVideoId()) || !copiedVideoIdSet.add(item.getVideoId())) {
                continue;
            }

            VideoUserCollectionsItem newItem = new VideoUserCollectionsItem();
            newItem.setCollectionsId(targetCollectionsId);
            newItem.setUserId(userId);
            newItem.setVideoId(item.getVideoId());
            itemListToCopy.add(newItem);
        }
        if (itemListToCopy.isEmpty()) {
            return;
        }

        for (VideoUserCollectionsItem item : itemListToCopy) {
            videoUserCollectionsItemMapper.insert(item);
        }

        videoUserCollectionsMapper.update(null, new LambdaUpdateWrapper<VideoUserCollections>()
                .setSql("item_count = item_count + " + itemListToCopy.size())
                .eq(VideoUserCollections::getId, targetCollectionsId));

        Map<Long, Long> videoIdToCountMap = itemListToCopy.stream()
                .collect(Collectors.groupingBy(VideoUserCollectionsItem::getVideoId, Collectors.counting()));
        videoIdToCountMap.forEach((videoId, count) ->
                videoMapper.update(null, new LambdaUpdateWrapper<Video>()
                        .setSql("favorites = favorites + " + count)
                        .eq(Video::getId, videoId))
        );

        for (VideoUserCollectionsItem item : itemListToCopy) {
            videoUserFavoriteCache.tryFavorite(userId, item.getVideoId());
        }
    }

    private List<VideoUserCollectionsItem> getCollectionsItemsForTransfer(Long userId, TransferCollectionsItemRequest transferCollectionsItemRequest) {
        if (transferCollectionsItemRequest.getIds() == null || transferCollectionsItemRequest.getIds().isEmpty()) {
            return Collections.emptyList();
        }

        List<VideoUserCollectionsItem> videoUserCollectionsItemList = videoUserCollectionsItemMapper.selectBatchIds(transferCollectionsItemRequest.getIds());
        for (VideoUserCollectionsItem item : videoUserCollectionsItemList) {
            if (!item.getUserId().equals(userId)) {
                throw new YHClientException("你没有权限！");
            }
        }
        return videoUserCollectionsItemList;
    }

    private void checkCollectionsOwner(Long userId, Long collectionsId) {
        VideoUserCollections collections = videoUserCollectionsMapper.selectOne(new LambdaQueryWrapper<VideoUserCollections>()
                .select(VideoUserCollections::getUserId)
                .eq(VideoUserCollections::getId, collectionsId));
        if (collections == null) {
            throw new YHClientException("目标收藏夹不存在！");
        }
        if (!collections.getUserId().equals(userId)) {
            throw new YHClientException("你没有权限！");
        }
    }

    private Set<Long> getTargetVideoIdSet(Long userId, Long targetCollectionsId, List<VideoUserCollectionsItem> sourceItemList) {
        List<Long> videoIdList = sourceItemList.stream()
                .map(VideoUserCollectionsItem::getVideoId)
                .distinct()
                .toList();
        if (videoIdList.isEmpty()) {
            return Collections.emptySet();
        }

        return videoUserCollectionsItemMapper.selectList(new LambdaQueryWrapper<VideoUserCollectionsItem>()
                        .select(VideoUserCollectionsItem::getVideoId)
                        .eq(VideoUserCollectionsItem::getUserId, userId)
                        .eq(VideoUserCollectionsItem::getCollectionsId, targetCollectionsId)
                        .in(VideoUserCollectionsItem::getVideoId, videoIdList))
                .stream()
                .map(VideoUserCollectionsItem::getVideoId)
                .collect(Collectors.toSet());
    }
}
