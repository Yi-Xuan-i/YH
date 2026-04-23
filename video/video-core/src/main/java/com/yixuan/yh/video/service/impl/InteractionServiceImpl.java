package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yixuan.yh.common.exception.YHServerException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.feign.UserPrivateClient;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.mapper.*;
import com.yixuan.yh.video.mapstruct.InteractionMapStruct;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.pojo.entity.*;
import com.yixuan.yh.video.pojo.entity.multi.CommentWithReceiver;
import com.yixuan.yh.video.pojo.mq.VideoCommentMessage;
import com.yixuan.yh.video.pojo.request.PostCommentRequest;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.video.pojo.response.GetDirectCommentResponse;
import com.yixuan.yh.video.pojo.response.GetReplyCommentResponse;
import com.yixuan.yh.video.service.CollectionsItemService;
import com.yixuan.yh.video.service.CollectionsService;
import com.yixuan.yh.video.service.InteractionService;
import com.yixuan.yh.video.template.FavoriteInteraction;
import com.yixuan.yh.video.template.LikeInteraction;
import org.apache.coyote.BadRequestException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private CollectionsService collectionsService;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private VideoUserCommentMapper videoUserCommentMapper;
    @Autowired
    private UserPrivateClient userPrivateClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CollectionsItemService collectionsItemService;

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
                            videoUserLike.setStatus(record.getStatus());

                            return videoUserLike;
                        })
                        .toList()
        );
    }

    @Override
    @Transactional
    public void favoriteBatch(VideoInteractionBatchRequest videoInteractionBatchRequest) {
        /* 更新视频收藏数 */
        videoMapper.updateFavoriteBatch(videoInteractionBatchRequest.getInteractionIncrList());
        /* 新增/删除用户收藏记录 */
        // 获取默认收藏夹
        Map<Long, Long> userToDefaultCollectionsMap = collectionsService.getDefaultCollectionsIdBatch(
                videoInteractionBatchRequest.getInteractionRecordList()
                        .stream()
                        .map(VideoInteractionBatchRequest.Record::getUserId)
                        .distinct()
                        .toList()
        );
        // 划分新增/删除记录
        List<Long> insertCollectionsIdList = new ArrayList<>();
        List<VideoUserCollectionsItem> toInsertList = videoInteractionBatchRequest.getInteractionRecordList()
                .stream()
                .filter(record -> record.getStatus() == InteractionStatus.FRONT)
                .map(record -> {
                    Long collectionsId = userToDefaultCollectionsMap.get(record.getUserId());
                    insertCollectionsIdList.add(collectionsId);

                    VideoUserCollectionsItem item = new VideoUserCollectionsItem();
                    item.setCollectionsId(collectionsId);
                    item.setVideoId(record.getVideoId());
                    item.setUserId(record.getUserId());
                    return item;
                })
                .toList();
        List<Long> deleteCollectionsIdList = new ArrayList<>();
        List<VideoUserCollectionsItem> toDeleteList = videoInteractionBatchRequest.getInteractionRecordList()
                .stream()
                .filter(record -> record.getStatus() == InteractionStatus.BACK)
                .map(record -> {
                    Long collectionsId = userToDefaultCollectionsMap.get(record.getUserId());
                    deleteCollectionsIdList.add(collectionsId);

                    VideoUserCollectionsItem item = new VideoUserCollectionsItem();
                    item.setCollectionsId(collectionsId);
                    item.setVideoId(record.getVideoId());
                    item.setUserId(record.getUserId());
                    return item;
                })
                .toList();
        // 执行数据库操作
        if (!toInsertList.isEmpty()) {
            collectionsService.update(new LambdaUpdateWrapper<VideoUserCollections>()
                    .setSql("item_count = item_count + 1")
                    .in(VideoUserCollections::getId, insertCollectionsIdList)
            );
            collectionsItemService.saveBatch(toInsertList);
        }
        if (!toDeleteList.isEmpty()) {
            collectionsService.update(new LambdaUpdateWrapper<VideoUserCollections>()
                    .setSql("item_count = item_count - 1")
                    .in(VideoUserCollections::getId, deleteCollectionsIdList)
            );
            collectionsItemService.remove(new LambdaQueryWrapper<VideoUserCollectionsItem>()
                    .apply("(collections_id, video_id, user_id) IN " +
                            toDeleteList.stream()
                                    .map(item -> String.format("(%d, %d, %d)",
                                            item.getCollectionsId(), item.getVideoId(), item.getUserId()))
                                    .collect(Collectors.joining(", ", "(", ")"))
                    )
            );
        }


    }

    @Override
    public String comment(Long videoId, Long userId, PostCommentRequest postCommentRequest) throws BadRequestException {
        // 判断视频是否存在（如果是回复不应该传递videoId）
        if (!videoMapper.selectIsExistById(videoId)) {
            throw new BadRequestException("视频不存在！");
        }

        // 获取根评论id
        Long rootId = null;

        // 如果 parentId 为空则为直接评论，根路径id为自己本身（反之去查询）
        if (postCommentRequest.getParentId() != null) {
            rootId = videoUserCommentMapper.selectRootIdById(postCommentRequest.getParentId());
        }

        // 存储评论数据到数据库
        VideoUserComment videoUserComment = InteractionMapStruct.INSTANCE.toVideoUserComment(postCommentRequest);
        videoUserComment.setId(snowflakeUtils.nextId());
        videoUserComment.setVideoId(videoId);
        videoUserComment.setUserId(userId);
        videoUserComment.setRootId(rootId == null ? videoUserComment.getId() : rootId);

        videoUserCommentMapper.insert(videoUserComment);

        // 发布评论事件（丢失风险）
        VideoCommentMessage videoCommentMessage = new VideoCommentMessage();
        videoCommentMessage.setId(videoUserComment.getId());
        videoCommentMessage.setVideoId(videoUserComment.getVideoId());
        videoCommentMessage.setUserId(videoUserComment.getUserId());
        videoCommentMessage.setRootId(rootId);
        videoCommentMessage.setContent(videoUserComment.getContent());

        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_INTERACTION_TOPIC_EXCHANGE, RabbitMQConstant.VIDEO_COMMENT_QUEUE_KEY, videoCommentMessage);

        return videoUserComment.getId().toString();
    }

    @Override
    public List<GetDirectCommentResponse> directComment(Long videoId, Long lastMinId) {
        List<VideoUserComment> videoUserCommentList = videoUserCommentMapper.selectDirectComment(videoId, lastMinId);
        // 判断是否非空
        if (videoUserCommentList.isEmpty()) {
            return Collections.emptyList();
        }
        // 转换实体类
        List<GetDirectCommentResponse> directCommentResponseList = videoUserCommentList
                .stream()
                .map(InteractionMapStruct.INSTANCE::toGetDirectCommentResponse)
                .toList();
        // 获取所有用户id
        List<Long> userIdList = directCommentResponseList.stream().map(GetDirectCommentResponse::getUserId).toList();
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
        directCommentResponseList.forEach(getCommentResponse -> {
            UserInfoInListResponse userInfo = userInfoMap.get(getCommentResponse.getUserId());
            getCommentResponse.setName(userInfo.getName());
            getCommentResponse.setAvatarUrl(userInfo.getAvatarUrl());
        });

        return directCommentResponseList;
    }

    @Override
    public List<GetReplyCommentResponse> replyComment(Long commentId, Long lastMaxId) {
        List<CommentWithReceiver> videoUserCommentList = videoUserCommentMapper.selectReplyComment(commentId, lastMaxId);
        // 转换格式
        List<GetReplyCommentResponse> replyCommentResponseList = videoUserCommentList
                .stream()
                .map(InteractionMapStruct.INSTANCE::toGetReplyCommentResponse)
                .toList();
        // 获取所有用户id
        List<Long> userIdList = replyCommentResponseList.stream()
                .flatMap(reply -> Stream.of(
                        reply.getSenderId(),
                        reply.getReceiverId()
                ))
                .distinct()
                .toList();
        // 查询用户数据
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
        // 完善用户数据
        for (GetReplyCommentResponse replyCommentResponse : replyCommentResponseList) {
            UserInfoInListResponse senderInfo = userInfoMap.get(replyCommentResponse.getSenderId());
            UserInfoInListResponse receiverInfo = userInfoMap.get(replyCommentResponse.getReceiverId());

            replyCommentResponse.setSenderName(senderInfo.getName());
            replyCommentResponse.setSenderAvatar(senderInfo.getAvatarUrl());
            replyCommentResponse.setReceiverName(receiverInfo.getName());
            replyCommentResponse.setReceiverAvatar(receiverInfo.getAvatarUrl());
        }

        return replyCommentResponseList;
    }
}
