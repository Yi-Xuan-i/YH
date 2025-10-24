package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.response.GetCollectionsResponse;

import java.util.List;

public interface CollectionsService {
    List<GetCollectionsResponse> getCollections(Long userId);

    String postCollections(Long userId, PostCollectionsRequest postCollectionsRequest);
}
