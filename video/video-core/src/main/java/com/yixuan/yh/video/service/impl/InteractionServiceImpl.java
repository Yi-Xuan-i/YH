package com.yixuan.yh.video.service.impl;

import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.video.service.InteractionService;
import com.yixuan.yh.video.template.FavoriteInteraction;
import com.yixuan.yh.video.template.LikeInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private LikeInteraction likeInteraction;
    @Autowired
    private FavoriteInteraction favoriteInteraction;

    @Override
    public void like(Long userId, Long videoId) throws Exception {
        likeInteraction.handle(userId, videoId, InteractionStatus.FRONT);
    }

    @Override
    public void unlike(Long userId, Long videoId) throws Exception {
        likeInteraction.handle(userId, videoId, InteractionStatus.BACK);
    }

    @Override
    public void favorite(Long userId, Long videoId) throws Exception {
        favoriteInteraction.handle(userId, videoId, InteractionStatus.FRONT);
    }

    @Override
    public void unfavorite(Long userId, Long videoId) throws Exception {
        favoriteInteraction.handle(userId, videoId, InteractionStatus.BACK);
    }

    @Override
    public void likeBatch(VideoInteractionBatchRequest videoInteractionBatchRequest) {
        videoMapper.updateLikeBatch(videoInteractionBatchRequest.getInteractionIncrList());
    }

    @Override
    public void favoriteBatch(VideoInteractionBatchRequest videoInteractionBatchRequest) {
        videoMapper.updateFavoriteBatch(videoInteractionBatchRequest.getInteractionIncrList());
    }
}
