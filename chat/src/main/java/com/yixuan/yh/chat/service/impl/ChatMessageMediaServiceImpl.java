package com.yixuan.yh.chat.service.impl;

import com.yixuan.yh.chat.entity.ChatMessage;
import com.yixuan.yh.chat.entity.ChatMessageMedia;
import com.yixuan.yh.chat.mapper.ChatMessageMediaMapper;
import com.yixuan.yh.chat.request.ChatMediaPresignRequest;
import com.yixuan.yh.chat.response.ChatMediaPresignResponse;
import com.yixuan.yh.chat.response.ChatMessageMediaResponse;
import com.yixuan.yh.chat.service.ChatMessageMediaService;
import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ChatMessageMediaServiceImpl implements ChatMessageMediaService {

    private static final Duration UPLOAD_URL_EXPIRE = Duration.ofMinutes(10);
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/png";
    private static final String DEFAULT_VIDEO_CONTENT_TYPE = "video/mp4";

    @Autowired
    private AWSUtils awsUtils;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private ChatMessageMediaMapper chatMessageMediaMapper;

    @Override
    public ChatMediaPresignResponse presignPutObject(ChatMediaPresignRequest request) {
        if (request == null) {
            throw new YHClientException("request cannot be empty");
        }
        ChatMessage.MessageType messageType = getMediaContentType(request.getMediaType());
        String objectKey = awsUtils.generateKey();
        String uploadContentType = getUploadContentType(messageType, request.getContentType());

        ChatMessageMedia media = new ChatMessageMedia();
        media.setId(snowflakeUtils.nextId());
        media.setMediaType(messageType.getCode());
        media.setUrl(objectKey);
        media.setCreatedAt(LocalDateTime.now());

        ChatMediaPresignResponse response = new ChatMediaPresignResponse();
        response.setMediaId(media.getId());
        response.setMediaType(media.getMediaType());
        response.setObjectKey(objectKey);
        response.setUploadUrl(awsUtils.presignPutObject(objectKey, uploadContentType, UPLOAD_URL_EXPIRE));
        response.setAccessUrl(awsUtils.generateAccessUrl(objectKey));

        if (messageType == ChatMessage.MessageType.VIDEO && StringUtils.hasText(request.getCoverContentType())) {
            String coverKey = awsUtils.generateKey();
            media.setCoverUrl(coverKey);
            response.setCoverObjectKey(coverKey);
            response.setCoverUploadUrl(awsUtils.presignPutObject(coverKey, request.getCoverContentType(), UPLOAD_URL_EXPIRE));
            response.setCoverAccessUrl(awsUtils.generateAccessUrl(coverKey));
        }

        chatMessageMediaMapper.insert(media);
        return response;
    }

    @Override
    public ChatMessageMediaResponse uploadCallback(Long mediaId) {
        ChatMessageMedia media = chatMessageMediaMapper.selectById(mediaId);
        if (media == null) {
            throw new YHClientException("media does not exist");
        }
        if (!awsUtils.isObjectExist(media.getUrl())) {
            throw new YHClientException("media file has not been uploaded");
        }
        if (StringUtils.hasText(media.getCoverUrl()) && !awsUtils.isObjectExist(media.getCoverUrl())) {
            throw new YHClientException("media cover has not been uploaded");
        }

        return toResponse(media);
    }

    @Override
    public ChatMessageMediaResponse toResponse(ChatMessageMedia media) {
        return new ChatMessageMediaResponse(
                media.getId(),
                media.getMediaType(),
                awsUtils.generateAccessUrl(media.getUrl()),
                StringUtils.hasText(media.getCoverUrl()) ? awsUtils.generateAccessUrl(media.getCoverUrl()) : null
        );
    }

    private ChatMessage.MessageType getMediaContentType(Integer mediaType) {
        if (mediaType != null && mediaType == ChatMessage.MessageType.IMAGE.getCode()) {
            return ChatMessage.MessageType.IMAGE;
        }
        if (mediaType != null && mediaType == ChatMessage.MessageType.VIDEO.getCode()) {
            return ChatMessage.MessageType.VIDEO;
        }
        throw new YHClientException("unsupported media type");
    }

    private String getUploadContentType(ChatMessage.MessageType messageType, String requestedContentType) {
        if (StringUtils.hasText(requestedContentType)) {
            return requestedContentType;
        }
        return messageType == ChatMessage.MessageType.IMAGE ? DEFAULT_IMAGE_CONTENT_TYPE : DEFAULT_VIDEO_CONTENT_TYPE;
    }
}
