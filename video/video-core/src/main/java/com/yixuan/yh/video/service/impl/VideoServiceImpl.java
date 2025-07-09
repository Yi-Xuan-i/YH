package com.yixuan.yh.video.service.impl;

import com.yixuan.mt.client.MTClient;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.entity.VideoTag;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.mapper.VideoTagMapper;
import com.yixuan.yh.video.mapper.VideoTagMpMapper;
import com.yixuan.yh.video.mapper.multi.VideoMultiMapper;
import com.yixuan.yh.video.mq.VideoPostMessage;
import com.yixuan.yh.video.pojo.request.PostVideoRequest;
import com.yixuan.yh.video.pojo.response.VideoMainResponse;
import com.yixuan.yh.video.pojo.response.VideoMainWithInteractionResponse;
import com.yixuan.yh.video.service.VideoService;
import org.apache.coyote.BadRequestException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoTagMapper videoTagMapper;

    @Autowired
    private VideoTagMpMapper videoTagMpMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Autowired
    private MTClient mtClient;
    @Autowired
    private VideoMultiMapper videoMultiMapper;

    @Override
    public List<VideoMainResponse> getVideos() {
         return videoMultiMapper.selectMainRandom();
    }

    @Override
    public List<VideoMainWithInteractionResponse> getVideosWithInteractionStatus(Long userId) {
        return videoMultiMapper.selectMainWithInteractionStatusRandom(userId);
    }

    @Override
    @Transactional
    public void postVideo(Long userId, PostVideoRequest postVideoRequest) throws IOException, InterruptedException {

        String videoUrl = mtClient.upload(postVideoRequest.getVideo());
        String coverUrl = mtClient.upload(postVideoRequest.getCover());
        Video video = new Video();
        video.setId(snowflakeUtils.nextId());
        video.setCreatorId(userId);
        video.setUrl(videoUrl);
        video.setCoverUrl(coverUrl);
        video.setDescription(postVideoRequest.getDescription());
        videoMapper.insert(video);

        /* 视频标签 */
        // “已存在”标签存在性检测
        List<String> tagNameList = null;
        if ((tagNameList = videoTagMapper.selectNameByIds(postVideoRequest.getAddedTagList())).size()
                != postVideoRequest.getAddedTagList().size()) {
            throw new BadRequestException("非法请求！");
        }

        // “不存在”标签新增
        List<VideoTag> existingTagList = null;
        List<VideoTag> needAddVideoTagList = null;
        if (!postVideoRequest.getAddedNewTagList().isEmpty()) {

            RLock lock = redissonClient.getLock("insertVideoTagLock");
            boolean isLock = lock.tryLock(1, 10, TimeUnit.SECONDS);
            if (isLock) {
                try {
                    existingTagList = videoTagMapper.selectSimplyByNames(postVideoRequest.getAddedNewTagList());

                    needAddVideoTagList = new ArrayList<>();
                    for (String newTagName : postVideoRequest.getAddedNewTagList()) {
                        boolean flag = false;
                        for (VideoTag tag: existingTagList) {
                            if (tag.getName().equals(newTagName)) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            continue;
                        }
                        VideoTag videoTag = new VideoTag();
                        videoTag.setId(snowflakeUtils.nextId());
                        videoTag.setName(newTagName);
                    }
                    videoTagMapper.insertBatch(needAddVideoTagList);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("服务器繁忙，请重试。");
            }

        }
        // 合并“所有类型”标签
        List<Long> allTagList = postVideoRequest.getAddedTagList();
        if (existingTagList != null) {
            for (VideoTag videoTag : existingTagList) {
                allTagList.add(videoTag.getId());
            }
        }
        if (needAddVideoTagList != null) {
            for (VideoTag videoTag : needAddVideoTagList) {
                allTagList.add(videoTag.getId());
            }
        }
        videoTagMpMapper.insertBatch(video.getId(), allTagList);

        /* 发送消息到消息队列 */
        VideoPostMessage videoPostMessage = new VideoPostMessage();
        videoPostMessage.setVideoId(video.getId());
        videoPostMessage.setVideoUrl(video.getUrl());
        tagNameList.addAll(postVideoRequest.getAddedNewTagList());
        videoPostMessage.setTagNameList(tagNameList);
        videoPostMessage.setDescription(postVideoRequest.getDescription());
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_POST_QUEUE, videoPostMessage);
    }
}
