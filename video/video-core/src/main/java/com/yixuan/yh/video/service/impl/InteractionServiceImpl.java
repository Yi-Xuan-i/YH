package com.yixuan.yh.video.service.impl;

import com.yixuan.yh.common.exception.YHServerException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.feign.UserPrivateClient;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.mapper.VideoUserCommentMapper;
import com.yixuan.yh.video.mapper.VideoUserFavoriteMapper;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.mapstruct.InteractionMapStruct;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserComment;
import com.yixuan.yh.video.pojo.entity.VideoUserFavorite;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import com.yixuan.yh.video.pojo.mq.VideoCommentMessage;
import com.yixuan.yh.video.pojo.request.PostCommentRequest;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.video.pojo.response.GetCommentResponse;
import com.yixuan.yh.video.service.InteractionService;
import com.yixuan.yh.video.template.FavoriteInteraction;
import com.yixuan.yh.video.template.LikeInteraction;
import org.apache.coyote.BadRequestException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private UserPrivateClient userPrivateClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

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
    public String comment(Long videoId, Long userId, PostCommentRequest postCommentRequest) throws BadRequestException {
        // 判断视频是否存在
        if (!videoMapper.selectIsExistById(videoId)) {
            throw new BadRequestException("视频不存在！");
        }

        // 存储评论数据到数据库
        VideoUserComment videoUserComment = InteractionMapStruct.INSTANCE.toVideoUserComment(postCommentRequest);
        videoUserComment.setId(snowflakeUtils.nextId());
        videoUserComment.setVideoId(videoId);
        videoUserComment.setUserId(userId);

        videoUserCommentMapper.insert(videoUserComment);

        // 发布评论事件（丢失风险）
        VideoCommentMessage videoCommentMessage = new VideoCommentMessage();
        videoCommentMessage.setId(videoUserComment.getId());
        videoCommentMessage.setParentId(videoUserComment.getParentId());
        videoCommentMessage.setVideoId(videoUserComment.getVideoId());
        videoCommentMessage.setUserId(videoUserComment.getUserId());
        videoCommentMessage.setContent(videoUserComment.getContent());

        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_COMMENT_FANOUT_EXCHANGE, "", videoCommentMessage);

        return videoUserComment.getId().toString();
    }

    @Override
    public List<GetCommentResponse> directComment(Long videoId) {
        List<VideoUserComment> videoUserCommentList = videoUserCommentMapper.selectDirectComment(videoId);
        // 判断是否非空
        if (videoUserCommentList.isEmpty()) {
            return Collections.emptyList();
        }
        // 转换实体类
        List<GetCommentResponse> getCommentResponseList = videoUserCommentList
                .stream()
                .map(InteractionMapStruct.INSTANCE::toGetCommentResponse)
                .toList();
        // 获取所有用户id
        List<Long> userIdList = getCommentResponseList.stream().map(GetCommentResponse::getUserId).toList();
        // 根据用户id查询用户数据
        Result<List<UserInfoInListResponse>> result = userPrivateClient.getUserInfoInList(userIdList);
        if (result.isError()) {
            throw new YHServerException(result.getMsg());
        }
        // 建立用户id到用户数据的字典
        List<UserInfoInListResponse> userInfoList = result.getData();
        Map<Long, UserInfoInListResponse> userInfoMap = new HashMap<>(userInfoList.size());
        for (UserInfoInListResponse userInfo : userInfoList) {
            userInfoMap.put(userInfo.getId(), userInfo);
        }
        // 完善comment数据
        getCommentResponseList.forEach(getCommentResponse -> {
            UserInfoInListResponse userInfo = userInfoMap.get(getCommentResponse.getUserId());
            getCommentResponse.setName(userInfo.getName());
            getCommentResponse.setAvatarUrl(userInfo.getAvatarUrl());
        });

        return getCommentResponseList;
    }
}
