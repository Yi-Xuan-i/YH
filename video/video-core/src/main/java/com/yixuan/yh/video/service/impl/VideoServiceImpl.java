package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.exception.YHServerException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.feign.UserFollowPrivateClient;
import com.yixuan.yh.user.feign.UserPreferencesPrivateClient;
import com.yixuan.yh.user.feign.UserPrivateClient;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.mapper.*;
import com.yixuan.yh.video.mapstruct.VideoMapStruct;
import com.yixuan.yh.video.pojo.entity.MessageOutbox;
import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.entity.VideoTag;
import com.yixuan.yh.video.mapper.multi.VideoMultiMapper;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithFavorite;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithInteractionStatus;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithLike;
import com.yixuan.yh.video.pojo.mq.VideoReviewMessage;
import com.yixuan.yh.video.pojo.entity.VideoUploadTask;
import com.yixuan.yh.video.pojo.request.GetPresignUrlRequest;
import com.yixuan.yh.video.pojo.response.*;
import com.yixuan.yh.video.pojo.request.PostVideoMessageRequest;
import com.yixuan.yh.video.service.VideoService;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private UserPreferencesPrivateClient userPreferencesPrivateClient;
    @Autowired
    private VideoMultiMapper videoMultiMapper;
    @Autowired
    private MilvusClientV2 milvusClient;
    @Autowired
    private UserFollowPrivateClient userFollowPrivateClient;
    @Autowired
    private AWSUtils awsUtils;
    @Autowired
    private VideoUploadTaskMapper videoUploadTaskMapper;
    @Autowired
    private UserPrivateClient userPrivateClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageOutBoxMapper messageOutBoxMapper;

    @Override
    public List<VideoMainResponse> getVideos(Long userId) {
        List<VideoWithInteractionStatus> videoWithInteractionStatusList = videoMultiMapper.selectMainRandom(userId);

        // 转换
        List<VideoMainResponse> videoMainResponseList = videoWithInteractionStatusList.stream()
                .map(status -> {
                    VideoMainResponse response = VideoMapStruct.INSTANCE.toVideoMainResponse(status);
                    response.setUrl(awsUtils.generateAccessUrl(response.getUrl()));
                    return response;
                })
                .toList();

        if (userId != null) {
            // 获取关注状态
            List<Long> followeeIdList = videoMainResponseList.stream().map(VideoMainResponse::getCreatorId).toList();
            List<Integer> sortedIndices = IntStream.range(0, followeeIdList.size())
                    .boxed()
                    .sorted(Comparator.comparingLong(followeeIdList::get))
                    .toList();
            List<Long> sortedFolloweeIdList = sortedIndices.stream().map(followeeIdList::get).toList();

            List<Boolean> followStatusList = userFollowPrivateClient.getFollowStatus(userId, sortedFolloweeIdList).getData();
            for (int i = 0; i < followStatusList.size(); i++) {
                videoMainResponseList.get(sortedIndices.get(i)).setIsFollowed(followStatusList.get(i));
            }
        }

        return videoMainResponseList;
    }

    @Override
    public VideoMainResponse getVideo(Long videoId) {
        return videoMultiMapper.selectMainOne(videoId);
    }

    @Override
    public String startUploadPart(Long userId, Integer totalChunks) {

        String key = awsUtils.generateKey();

        // 调用开启分片上传（该分片有可能不被记录然后不会被清理）
        String uploadId = awsUtils.createMultipartUpload(key, "video/mp4");

        // 插入 task 对象
        VideoUploadTask task = new VideoUploadTask();
        task.setUserId(userId);
        task.setKey(key);
        task.setTotalChunks(totalChunks);
        task.setUploadId(uploadId);
        task.setUploadType(VideoUploadTask.UploadType.MULTIPART);
        task.setStatus(VideoUploadTask.UploadStatus.UPLOADING);
        task.setExpireAt(LocalDateTime.now().plusMinutes(15));

        videoUploadTaskMapper.insert(task);

        return task.getId().toString();
    }

    @Override
    public Map<Integer, String> presignUploadPart(Long userId, GetPresignUrlRequest getPresignUrlRequest) {
        VideoUploadTask videoUploadTask = videoUploadTaskMapper.selectForPresign(getPresignUrlRequest.getTaskId());

        if (videoUploadTask == null || !videoUploadTask.getUserId().equals(userId)) {
            throw new YHClientException("异常的上传任务！");
        }

        videoUploadTaskMapper.update(new LambdaUpdateWrapper<VideoUploadTask>()
                .set(VideoUploadTask::getExpireAt, LocalDateTime.now().plusMinutes(10)));

        return awsUtils.presignUploadPart(videoUploadTask.getKey(), videoUploadTask.getUploadId(), getPresignUrlRequest.getPartNumberList(), Duration.ofMinutes(10));
    }

    @Override
    public PresignPutObjectResponse presignPutObject(Long userId) {
        // 插入 task 对象
        VideoUploadTask task = new VideoUploadTask();
        task.setUserId(userId);
        task.setKey(awsUtils.generateKey());
        task.setTotalChunks(1);
        task.setUploadType(VideoUploadTask.UploadType.NORMAL);
        task.setStatus(VideoUploadTask.UploadStatus.UPLOADING);
        task.setExpireAt(LocalDateTime.now().plusMinutes(15));

        videoUploadTaskMapper.insert(task);

        return new PresignPutObjectResponse(task.getId(), awsUtils.presignPutObject(task.getKey(), "video/mp4", Duration.ofMinutes(15)));
    }

    @Override
    @Transactional
    public PostVideoEndResponse postVideoEnd(Long userId, Long taskId) {
        VideoUploadTask videoUploadTask = videoUploadTaskMapper.selectForComplete(taskId);

        if (videoUploadTask == null || !videoUploadTask.getUserId().equals(userId)) {
            throw new YHClientException("异常的上传任务！");
        }

        if (videoUploadTask.getUploadType() == VideoUploadTask.UploadType.MULTIPART) {
            List<Integer> uploadedPartNumberList = awsUtils.completeMultipartUpload(videoUploadTask.getKey(), videoUploadTask.getUploadId(), videoUploadTask.getTotalChunks());

            // 代表实际上仍有分片未上传
            if (!uploadedPartNumberList.isEmpty()) {
                return new PostVideoEndResponse(uploadedPartNumberList, null);
            }
        }

        // 已经上传完毕，正式插入视频表
        Video video = new Video();
        video.setCreatorId(userId);
        video.setUrl(videoUploadTask.getKey());
        video.setStatus(Video.VideoStatus.DRAFT);

        videoMapper.insert(video);

        // 修改任务表状态
        videoUploadTaskMapper.update(new LambdaUpdateWrapper<VideoUploadTask>()
                .set(VideoUploadTask::getStatus, VideoUploadTask.UploadStatus.COMPLETED)
                .eq(VideoUploadTask::getId, taskId));

        return new PostVideoEndResponse(null, video.getId());
    }

    private List<VideoMainResponse> getRecommendedVideos(Long userId) {
        // 获取用户视频偏好向量
        float[] vector = userPreferencesPrivateClient.getUserVideoPreferences(userId).getData();

        // 根据用户视频偏好向量搜索相似视频
        SearchResp searchR = milvusClient.search(SearchReq.builder()
                .collectionName("video_collection")
                .annsField("recommend_feature")
                .data(Collections.singletonList(new FloatVec(vector)))
                .topK(10)
                .outputFields(List.of("video_id"))
                .searchParams(Map.of("metric_type", "COSINE", "params", Map.of("nprobe", 10)))
                .build());
        List<List<SearchResp.SearchResult>> searchResults = searchR.getSearchResults();
        for (List<SearchResp.SearchResult> results : searchResults) {
            for (SearchResp.SearchResult result : results) {
                System.out.println(result.getId());
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void postVideoMessage(Long userId, PostVideoMessageRequest postVideoMessageRequest) throws IOException {
        /* 获取所需视频数据 */
        Video video = videoMapper.selectOne(new LambdaQueryWrapper<Video>()
                .select(Video::getCreatorId, Video::getUrl, Video::getStatus)
                .eq(Video::getId, postVideoMessageRequest.getVideoId()));

        /* 校验 */
        if (video == null || !video.getCreatorId().equals(userId)) {
            throw new YHClientException("上传视频数据异常！");
        }
        if (video.getStatus() != Video.VideoStatus.DRAFT) {
            throw new YHClientException("请勿重复提交！");
        }

        /* 乐观锁 */
        String coverObjectKey = "cover/" + postVideoMessageRequest.getVideoId().toString();
        int result = videoMapper.update(null, new LambdaUpdateWrapper<Video>()
                .set(Video::getStatus, Video.VideoStatus.PENDING_REVIEW)
                .set(Video::getCoverUrl, coverObjectKey)
                .set(Video::getDescription, postVideoMessageRequest.getDescription())
                .eq(Video::getId, postVideoMessageRequest.getVideoId())
                .eq(Video::getStatus, Video.VideoStatus.DRAFT));
        if (result == 0) {
            throw new YHClientException("请勿重复提交！");
        }

        /* 上传封面（使用固定key解决文件上传幂等问题 ） */
        awsUtils.putObject(coverObjectKey, postVideoMessageRequest.getCover());

        /* 补充视频数据 */
        video.setId(postVideoMessageRequest.getVideoId());
        video.setCoverUrl(coverObjectKey);
        video.setDescription(postVideoMessageRequest.getDescription());
        videoMapper.updateById(video);

        /* 视频标签 */
        handleTag(postVideoMessageRequest);

        /* 发送消息到消息队列 */
        // 构建消息对象
        VideoReviewMessage videoReviewMessage = new VideoReviewMessage();
        videoReviewMessage.setVideoId(video.getId());
        videoReviewMessage.setVideoUrl(video.getUrl());
        videoReviewMessage.setTagNameList(postVideoMessageRequest.getAddedTagList());
        videoReviewMessage.setDescription(postVideoMessageRequest.getDescription());

        // 插入本地消息表
        MessageOutbox messageOutbox = new MessageOutbox();
        messageOutbox.setBusinessId(video.getId());
        messageOutbox.setRoutingKey(RabbitMQConstant.VIDEO_REVIEW_QUEUE);
        messageOutbox.setMessageBody(objectMapper.writeValueAsString(videoReviewMessage));
        messageOutbox.setStatus(MessageOutbox.OutboxStatus.PENDING);
        messageOutBoxMapper.insert(messageOutbox);

        // 注册事务提交后发送消息
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        rabbitTemplate.convertAndSend("", RabbitMQConstant.VIDEO_REVIEW_QUEUE,
                                messageOutbox.getMessageBody(),
                                message -> {
                                    // 手动设置消息头为 application/json
                                    message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
                                    return message;
                                },
                                new CorrelationData(messageOutbox.getId().toString())
                        );
                    }
                }
        );
    }

    private void handleTag(PostVideoMessageRequest postVideoMessageRequest) {
        // 查询已存在标签
        List<VideoTag> videoTagList = videoTagMapper
                .selectSimplyByNames(postVideoMessageRequest.getAddedTagList());

        // 已存在标签 name -> id
        Map<String, Long> existTagMap = videoTagList.stream()
                .collect(Collectors.toMap(VideoTag::getName, VideoTag::getId));

        // 分类：已存在 / 不存在
        List<Long> existTagIdList = new ArrayList<>();
        List<String> notExistTagNameList = new ArrayList<>();

        for (String tagName : postVideoMessageRequest.getAddedTagList()) {
            if (existTagMap.containsKey(tagName)) {
                existTagIdList.add(existTagMap.get(tagName));
            } else {
                notExistTagNameList.add(tagName);
            }
        }

        // 插入不存在的标签
        List<Long> newTagIdList = new ArrayList<>();
        if (!notExistTagNameList.isEmpty()) {
            List<VideoTag> newTags = notExistTagNameList.stream().map(name -> {
                VideoTag tag = new VideoTag();
                tag.setId(snowflakeUtils.nextId());
                tag.setName(name);
                tag.setCreatedTime(LocalDateTime.now());
                return tag;
            }).collect(Collectors.toList());

            // 批量插入
            videoTagMapper.insertBatch(newTags);

            // 插入后需要拿到 id
            newTagIdList = newTags.stream()
                    .map(VideoTag::getId)
                    .toList();
        }

        // 汇总所有标签ID
        List<Long> allTagIdList = new ArrayList<>();
        allTagIdList.addAll(existTagIdList);
        allTagIdList.addAll(newTagIdList);

        // 建立视频-标签关联
        videoTagMpMapper.insertBatch(postVideoMessageRequest.getVideoId(), allTagIdList);
    }

    @Override
    public void putVideoStatusToPublished(Long videoId) {
        videoTagMapper.updateStatus(videoId, Video.VideoStatus.PUBLISHED);
    }

    @Override
    public List<GetUploadedVideoResponse> getUploadedVideo(Long userId) {
        return videoMapper.selectUploadedVideoByUserId(userId)
                .stream()
                .map(VideoMapStruct.INSTANCE::toGetUploadedVideoResponse)
                .toList();
    }

    @Override
    public List<GetPublishedVideoResponse> getPublishedVideo(Long userId, Long lastMinId) {
        return videoMapper.selectPublishedVideoByUserId(userId, lastMinId)
                .stream()
                .map(VideoMapStruct.INSTANCE::toGetPublishedVideoResponse)
                .toList();
    }

    @Override
    public List<GetProcessingVideoResponse> getProcessingVideo(Long userId, Long lastMinId) {
        return videoMapper.selectProcessingVideoByUserId(userId, lastMinId)
                .stream()
                .map(VideoMapStruct.INSTANCE::toGetProcessingVideoResponse)
                .toList();
    }

    @Override
    public List<GetRejectedVideoResponse> getRejectedVideo(Long userId, Long lastMinId) {
        return videoMapper.selectRejectedVideoByUserId(userId, lastMinId)
                .stream()
                .map(VideoMapStruct.INSTANCE::toGetRejectedVideoResponse)
                .toList();
    }

    @Override
    public List<GetLikeVideoResponse> getLikeVideo(Long userId, Long lastMinId) {
        // 获取数据
        List<VideoWithLike> videoWithLikeList = videoMapper.selectLikeVideoByUserId(userId, lastMinId);
        if (videoWithLikeList.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换格式
        List<GetLikeVideoResponse> likeVideoResponseList = videoMapper.selectLikeVideoByUserId(userId, lastMinId)
                .stream()
                .map(VideoMapStruct.INSTANCE::toGetLikeVideoResponse)
                .toList();

        // 提取 creatorId
        List<Long> creatorIdList = likeVideoResponseList.stream()
                .map(GetLikeVideoResponse::getCreatorId)
                .toList();

        // 获取对应 creatorName
        Result<Map<Long, String>> result = userPrivateClient.getNameBatch(creatorIdList);
        if (result.isError()) {
            throw new YHServerException(result.getMsg());
        }
        Map<Long, String> idToNameMap = result.getData();

        // 完善数据
        likeVideoResponseList.forEach(response -> {
            response.setCreatorName(idToNameMap.get(response.getCreatorId()));
        });

        return likeVideoResponseList;
    }

    @Override
    public List<GetFavoriteVideoResponse> getFavoriteVideo(Long userId, Long lastMinId) {
        // 获取数据
        List<VideoWithFavorite> videoWithFavoriteList = videoMapper.selectFavoriteVideoByUserId(userId, lastMinId);
        if (videoWithFavoriteList.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换格式
        List<GetFavoriteVideoResponse> favoriteVideoResponseList = videoWithFavoriteList
                .stream()
                .map(VideoMapStruct.INSTANCE::toGetFavoriteVideoResponse)
                .toList();

        // 提取 creatorId
        List<Long> creatorIdList = favoriteVideoResponseList.stream()
                .map(GetFavoriteVideoResponse::getCreatorId)
                .toList();

        // 获取对应 creatorName
        Result<Map<Long, String>> result = userPrivateClient.getNameBatch(creatorIdList);
        if (result.isError()) {
            throw new YHServerException(result.getMsg());
        }
        Map<Long, String> idToNameMap = result.getData();

        // 完善数据
        favoriteVideoResponseList.forEach(response -> {
            response.setCreatorName(idToNameMap.get(response.getCreatorId()));
        });

        return favoriteVideoResponseList;
    }
}
