package com.yixuan.yh.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.request.PutCollectionsRequest;
import com.yixuan.yh.video.pojo.response.GetCollectionsItemResponse;
import com.yixuan.yh.video.pojo.response.GetCollectionsResponse;

import java.util.List;
import java.util.Map;

public interface CollectionsService extends IService<VideoUserCollections> {
    List<GetCollectionsResponse> getCollections(Long userId, Long lastMinId);

    List<GetCollectionsItemResponse> getCollectionsItemList(Long userId, Long collectionsId, Long lastMinId);

    String postCollections(Long userId, PostCollectionsRequest postCollectionsRequest);

    void putCollections(Long collectionsId, PutCollectionsRequest putCollectionsRequest);

    void deleteCollections(Long userId, Long collectionsId);

    Map<Long, Long> getDefaultCollectionsIdBatch(List<Long> list);
}
