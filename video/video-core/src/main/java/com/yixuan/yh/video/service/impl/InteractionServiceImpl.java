package com.yixuan.yh.video.service.impl;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.cache.VideoUserLikeCache;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.constant.RedisConstant;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.mq.VideoLikeMessage;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import com.yixuan.yh.video.pojo.request.VideoLikeBatchRequest;
import com.yixuan.yh.video.service.InteractionService;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private VideoUserLikeCache videoUserLikeCache;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public void like(Long userId, Long videoId) throws Exception {

        Lock lock = redissonClient.getLock(RedisConstant.VIDEO_LIKE_LOCK_KEY_PREFIX + userId);
        boolean isLock = lock.tryLock(2, TimeUnit.SECONDS);
        if (isLock) {
            try {
                // 用户是否当前状态为未点赞
                if (videoUserLikeCache.isLike(userId, videoId, VideoUserLike.Status.UNLIKE)) {
                    return;
                }
                // 保存到数据库
                VideoUserLike videoUserLike = new VideoUserLike();
                videoUserLike.setId(snowflakeUtils.nextId());
                videoUserLike.setUserId(userId);
                videoUserLike.setVideoId(videoId);
                videoUserLike.setStatus(VideoUserLike.Status.LIKE);
                videoUserLikeMapper.insert(videoUserLike);
                // 保存到Redis
                stringRedisTemplate.opsForHash().put(RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId, videoId.toString(), "1");
            } finally {
                lock.unlock();
            }
        } else {
            throw new Exception("服务器繁忙，请重试！");
        }

        // 点赞信息发送到消息队列异步处理
        VideoLikeMessage videoLikeMessage = new VideoLikeMessage();
        videoLikeMessage.setUserId(userId);
        videoLikeMessage.setVideoId(videoId);
        videoLikeMessage.setStatus(VideoLikeMessage.Status.LIKE);

        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_LIKE_QUEUE, videoLikeMessage);
    }

    @Override
    @Transactional
    public void unlike(Long userId, Long videoId) throws Exception {
        Lock lock = redissonClient.getLock(RedisConstant.VIDEO_LIKE_LOCK_KEY_PREFIX + userId);
        boolean isLock = lock.tryLock(2, TimeUnit.SECONDS);
        if (isLock) {
            try {
                // 用户是否当前状态为已点赞
                if (!videoUserLikeCache.isLike(userId, videoId, VideoUserLike.Status.LIKE)) {
                    return;
                }

                // 保存到数据库
                VideoUserLike videoUserLike = new VideoUserLike();
                videoUserLike.setId(snowflakeUtils.nextId());
                videoUserLike.setUserId(userId);
                videoUserLike.setVideoId(videoId);
                videoUserLike.setStatus(VideoUserLike.Status.UNLIKE);
                videoUserLikeMapper.insert(videoUserLike);
                // 保存到Redis
                stringRedisTemplate.opsForHash().put(RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId, videoId.toString(), "0");

            } finally {
                lock.unlock();
            }
        } else {
            throw new Exception("服务器繁忙，请重试！");
        }

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
//        List<VideoUserLike> videoUserLikeList = new ArrayList<>(videoLikeBatchRequest.getLikeRecordList().size());
//        for (VideoLikeBatchRequest.LikeRecord likeRecord : videoLikeBatchRequest.getLikeRecordList()) {
//            VideoUserLike videoUserLike = InteractionMapStruct.INSTANCE.videoRecordToVideoUserLike(likeRecord);
//            videoUserLike.setId(snowflakeUtils.nextId());
//
//            videoUserLikeList.add(videoUserLike);
//        }
//        videoUserLikeMapper.insertBatch(videoUserLikeList);
    }
}
