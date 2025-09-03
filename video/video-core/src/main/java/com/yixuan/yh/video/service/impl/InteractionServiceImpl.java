package com.yixuan.yh.video.service.impl;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.mapper.VideoUserCommentMapper;
import com.yixuan.yh.video.mapper.VideoUserFavoriteMapper;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.mapstruct.InteractionMapStruct;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserComment;
import com.yixuan.yh.video.pojo.entity.VideoUserFavorite;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import com.yixuan.yh.video.pojo.request.PostCommentRequest;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.video.pojo.response.GetCommentResponse;
import com.yixuan.yh.video.service.InteractionService;
import com.yixuan.yh.video.template.FavoriteInteraction;
import com.yixuan.yh.video.template.LikeInteraction;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private LikeInteraction likeInteraction;
    @Autowired
    private FavoriteInteraction favoriteInteraction;
    @Autowired
    private VideoUserLikeMapper videoUserLikeMapper;
    @Autowired
    private VideoUserFavoriteMapper videoUserFavoriteMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private VideoUserCommentMapper videoUserCommentMapper;

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
    @Transactional
    public void likeBatch(VideoInteractionBatchRequest videoInteractionBatchRequest) {
        videoMapper.updateLikeBatch(videoInteractionBatchRequest.getInteractionIncrList());
        videoUserLikeMapper.insertBatch(
                videoInteractionBatchRequest.getInteractionRecordList()
                        .stream()
                        .map(record -> {
                            VideoUserLike videoUserLike = new VideoUserLike();
                            videoUserLike.setId(snowflakeUtils.nextId());
                            videoUserLike.setVideoId(record.getVideoId());
                            videoUserLike.setUserId(record.getUserId());
                            videoUserLike.setStatus(record.getStatus().equals(VideoInteractionBatchRequest.Record.Status.LIKE) ? InteractionStatus.FRONT : InteractionStatus.BACK);

                            return videoUserLike;
                        })
                        .toList()
        );
    }

    @Override
    @Transactional
    public void favoriteBatch(VideoInteractionBatchRequest videoInteractionBatchRequest) {
        videoMapper.updateFavoriteBatch(videoInteractionBatchRequest.getInteractionIncrList());
        videoUserFavoriteMapper.insertBatch(
                videoInteractionBatchRequest.getInteractionRecordList()
                        .stream()
                        .map(record -> {
                            VideoUserFavorite videoUserFavorite = new VideoUserFavorite();
                            videoUserFavorite.setId(snowflakeUtils.nextId());
                            videoUserFavorite.setVideoId(record.getVideoId());
                            videoUserFavorite.setUserId(record.getUserId());
                            videoUserFavorite.setStatus(record.getStatus().equals(VideoInteractionBatchRequest.Record.Status.LIKE) ? InteractionStatus.FRONT : InteractionStatus.BACK);

                            return videoUserFavorite;
                        })
                        .toList()
        );
    }

    @Override
    public void comment(Long videoId, Long userId, PostCommentRequest postCommentRequest) throws BadRequestException {
        // 判断视频是否存在
        if (!videoMapper.selectIsExistById(videoId)) {
            throw new BadRequestException("视频不存在！");
        }

        VideoUserComment videoUserComment = InteractionMapStruct.INSTANCE.toVideoUserComment(postCommentRequest);
        videoUserComment.setId(snowflakeUtils.nextId());
        videoUserComment.setVideoId(videoId);
        videoUserComment.setUserId(userId);

        videoUserCommentMapper.insert(videoUserComment);
    }

    @Override
    public List<GetCommentResponse> directComment(Long videoId) {
        return videoUserCommentMapper.selectDirectComment(videoId)
                .stream()
                .map(InteractionMapStruct.INSTANCE::toGetCommentResponse)
                .toList();
    }
}
