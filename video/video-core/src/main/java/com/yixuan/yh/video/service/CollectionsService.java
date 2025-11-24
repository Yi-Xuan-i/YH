package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.request.PutCollectionsRequest;
import com.yixuan.yh.video.pojo.response.GetCollectionsItemResponse;
import com.yixuan.yh.video.pojo.response.GetCollectionsResponse;

import java.util.List;

public interface CollectionsService {
    List<GetCollectionsResponse> getCollections(Long userId, Long lastMinId);

    List<GetCollectionsItemResponse> getCollectionsItemList(Long userId, Long collectionsId);

    String postCollections(Long userId, PostCollectionsRequest postCollectionsRequest);

    void putCollections(Long collectionsId, PutCollectionsRequest putCollectionsRequest);

    void deleteCollections(Long collectionsId);
}
