package com.yixuan.yh.video.service.impl;

import com.yixuan.mt.client.MTClient;
import com.yixuan.yh.common.exception.YHServerException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.MinioUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.feign.UserFollowPrivateClient;
import com.yixuan.yh.user.feign.UserPreferencesPrivateClient;
import com.yixuan.yh.user.feign.UserPrivateClient;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.mapper.*;
import com.yixuan.yh.video.mapstruct.VideoMapStruct;
import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.entity.VideoTag;
import com.yixuan.yh.video.mapper.multi.VideoMultiMapper;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithFavorite;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithInteractionStatus;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithLike;
import com.yixuan.yh.video.pojo.mq.VideoPostMessage;
import com.yixuan.yh.video.pojo.entity.VideoUploadTask;
import com.yixuan.yh.video.pojo.response.*;
import com.yixuan.yh.video.pojo.request.PostVideoMessageRequest;
import com.yixuan.yh.video.service.VideoService;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import io.minio.ComposeSource;
import io.minio.errors.*;
import org.apache.coyote.BadRequestException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private MTClient mtClient;
    @Autowired
    private UserPreferencesPrivateClient userPreferencesPrivateClient;
    @Autowired
    private VideoMultiMapper videoMultiMapper;
    @Autowired
    private MilvusClientV2 milvusClient;
    @Autowired
    private UserFollowPrivateClient userFollowPrivateClient;
    @Autowired
    private MinioUtils minioUtils;
    @Autowired
    private VideoUploadTaskMapper videoUploadTaskMapper;
    @Autowired
    private UserPrivateClient userPrivateClient;

    @Override
    public List<VideoMainResponse> getVideos(Long userId) {
        List<VideoWithInteractionStatus> videoWithInteractionStatusList = videoMultiMapper.selectMainRandom(userId);

        // 转换
        List<VideoMainResponse> videoMainResponseList = videoWithInteractionStatusList.stream().map(VideoMapStruct.INSTANCE::toVideoMainResponse).toList();

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
    public String postVideoStart(Long userId, Long fileSize, Integer totalChunks) {
        Long uploadId = snowflakeUtils.nextId();

        VideoUploadTask videoUploadTask = new VideoUploadTask();
        videoUploadTask.setId(uploadId);
        videoUploadTask.setUserId(userId);
        videoUploadTask.setFileSize(fileSize);
        videoUploadTask.setTotalChunks(totalChunks);
        videoUploadTask.setChunkBitmap(new byte[]{0});

        videoUploadTaskMapper.insert(videoUploadTask);

        return uploadId.toString();
    }

    @Override
    public void postVideo(Long userId, Long uploadId, Long partNumber, MultipartFile file) throws Exception {
        minioUtils.uploadPart(uploadId, partNumber, file);
    }

    @Override
    public void postVideoEnd(Long uploadId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<ComposeSource> composeSourceList = new ArrayList<>();
        composeSourceList.add(ComposeSource.builder().bucket(minioUtils.getBucket()).object("3-0").build());
        composeSourceList.add(ComposeSource.builder().bucket(minioUtils.getBucket()).object("3-1").build());

//        System.out.println(minioUtils.completeUploadPart(composeSourceList));
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
    public void postVideoMessage(Long userId, PostVideoMessageRequest postVideoMessageRequest) throws Exception {

        String videoUrl = minioUtils.upload(postVideoMessageRequest.getVideo());
        String coverUrl = minioUtils.upload(postVideoMessageRequest.getCover());
        Video video = new Video();
        video.setId(snowflakeUtils.nextId());
        video.setCreatorId(userId);
        video.setUrl(videoUrl);
        video.setCoverUrl(coverUrl);
        video.setDescription(postVideoMessageRequest.getDescription());
        videoMapper.insert(video);

        /* 视频标签 */
        // “已存在”标签存在性检测
        List<String> tagNameList = null;
        if ((tagNameList = videoTagMapper.selectNameByIds(postVideoMessageRequest.getAddedTagList())).size()
                != postVideoMessageRequest.getAddedTagList().size()) {
            throw new BadRequestException("非法请求！");
        }

        // “不存在”标签新增
        List<VideoTag> existingTagList = null;
        List<VideoTag> needAddVideoTagList = null;
        if (!postVideoMessageRequest.getAddedNewTagList().isEmpty()) {

            RLock lock = redissonClient.getLock("insertVideoTagLock");
            boolean isLock = lock.tryLock(1, 10, TimeUnit.SECONDS);
            if (isLock) {
                try {
                    existingTagList = videoTagMapper.selectSimplyByNames(postVideoMessageRequest.getAddedNewTagList());

                    needAddVideoTagList = new ArrayList<>();
                    for (String newTagName : postVideoMessageRequest.getAddedNewTagList()) {
                        boolean flag = false;
                        for (VideoTag tag : existingTagList) {
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
        List<Long> allTagList = postVideoMessageRequest.getAddedTagList();
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
        tagNameList.addAll(postVideoMessageRequest.getAddedNewTagList());
        videoPostMessage.setTagNameList(tagNameList);
        videoPostMessage.setDescription(postVideoMessageRequest.getDescription());
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_POST_QUEUE, videoPostMessage);
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
