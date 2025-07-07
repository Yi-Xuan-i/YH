package com.yixuan.yh.video.service.impl;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.mapstruct.InteractionMapStruct;
import com.yixuan.yh.video.mq.VideoLikeMessage;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import com.yixuan.yh.video.pojo.request.VideoLikeBatchRequest;
import com.yixuan.yh.video.service.InteractionService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private VideoUserLikeMapper videoUserLikeMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Override
    public void like(Long userId, Long videoId) throws Exception {
        // 用户是否当前状态为未点赞
        if (videoUserLikeMapper.isLike(userId, videoId)) {
            throw new Exception("你已经点过赞！");
        }

        // 可以缓存用户点赞状态

        // 点赞信息发送到消息队列异步处理
        VideoLikeMessage videoLikeMessage = new VideoLikeMessage();
        videoLikeMessage.setUserId(userId);
        videoLikeMessage.setVideoId(videoId);
        videoLikeMessage.setStatus(VideoLikeMessage.Status.LIKE);

        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_LIKE_QUEUE, videoLikeMessage);
    }

    @Override
    public void unlike(Long userId, Long videoId) {
        // 可以缓存用户点赞状态

        // 点赞信息发送到消息队列异步处理
        VideoLikeMessage videoLikeMessage = new VideoLikeMessage();
        videoLikeMessage.setUserId(userId);
        videoLikeMessage.setVideoId(videoId);
        videoLikeMessage.setStatus(VideoLikeMessage.Status.UNLIKE);

        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_LIKE_QUEUE, videoLikeMessage);
    }

    @Override
    @Transactional
    public void likeBatch(VideoLikeBatchRequest videoLikeBatchRequest) {
        // 增加点赞数
        videoMapper.updateBatch(videoLikeBatchRequest.getLikeIncrList());
        // 插入点赞记录
        List<VideoUserLike> videoUserLikeList = new ArrayList<>(videoLikeBatchRequest.getLikeRecordList().size());
        for (VideoLikeBatchRequest.LikeRecord likeRecord : videoLikeBatchRequest.getLikeRecordList()) {
            VideoUserLike videoUserLike = InteractionMapStruct.INSTANCE.videoRecordToVideoUserLike(likeRecord);
            videoUserLike.setId(snowflakeUtils.nextId());

            videoUserLikeList.add(videoUserLike);
        }
        videoUserLikeMapper.insertBatch(videoUserLikeList);
    }
}
