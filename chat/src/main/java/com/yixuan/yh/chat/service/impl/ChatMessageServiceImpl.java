package com.yixuan.yh.chat.service.impl;

import com.yixuan.yh.chat._enum.ConversationUserNumber;
import com.yixuan.yh.chat.entity.ChatConversation;
import com.yixuan.yh.chat.entity.ChatMessage;
import com.yixuan.yh.chat.entity.ChatMessageMedia;
import com.yixuan.yh.chat.mapper.ChatConversationMapper;
import com.yixuan.yh.chat.mapper.ChatMessageMapper;
import com.yixuan.yh.chat.mapper.ChatMessageMediaMapper;
import com.yixuan.yh.chat.service.ChatMessageMediaService;
import com.yixuan.yh.chat.service.ChatMessageService;
import com.yixuan.yh.chat.websocket.pojo.ChatACKMessage;
import com.yixuan.yh.chat.websocket.pojo.ChatReceiveMessage;
import com.yixuan.yh.chat.websocket.pojo.ChatSendMessage;
import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private ChatConversationMapper chatConversationMapper;
    @Autowired
    private ChatMessageMediaMapper chatMessageMediaMapper;
    @Autowired
    private ChatMessageMediaService chatMessageMediaService;
    @Autowired
    private AWSUtils awsUtils;

    @Override
    @Transactional
    public void handleChatMessage(ChatSendMessage sendMessage, Long senderId) {
        ChatConversation conversation = chatConversationMapper.selectBothIdById(sendMessage.getConversationId());
        if (conversation == null) {
            return;
        }

        ConversationUserNumber senderNumber = getConversationUserNumber(conversation, senderId);
        if (senderNumber == null) {
            return;
        }

        ChatMessage.MessageType messageType = resolveContentType(sendMessage);
        ChatMessageMedia media = null;
        String content = sendMessage.getContent();
        if (messageType == ChatMessage.MessageType.IMAGE || messageType == ChatMessage.MessageType.VIDEO) {
            media = resolveUploadedMedia(sendMessage.getMediaId(), messageType);
            content = getMediaMessageLabel(messageType);
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(snowflakeUtils.nextId());
        chatMessage.setConversationId(sendMessage.getConversationId());
        chatMessage.setSenderId(senderId);
        chatMessage.setContent(content);
        chatMessage.setMessageType(messageType);
        chatMessage.setCreatedTime(LocalDateTime.now());
        chatMessageMapper.insert(chatMessage);

        if (media != null) {
            int updated = chatMessageMediaMapper.bindMessage(media.getId(), chatMessage.getId());
            if (updated == 0) {
                throw new YHClientException("media has already been used");
            }
        }

        sendAckMessage(sendMessage, senderId);
        Long receiverId = updateReceiverUnreadCount(sendMessage.getConversationId(), conversation, senderNumber);
        sendReceiveMessage(receiverId, senderId, sendMessage.getConversationId(), content, messageType, media, chatMessage.getCreatedTime());
    }

    private ConversationUserNumber getConversationUserNumber(ChatConversation conversation, Long senderId) {
        if (senderId.equals(conversation.getUser1Id())) {
            return ConversationUserNumber.USER1;
        }
        if (senderId.equals(conversation.getUser2Id())) {
            return ConversationUserNumber.USER2;
        }
        return null;
    }

    private ChatMessage.MessageType resolveContentType(ChatSendMessage sendMessage) {
        if (sendMessage.getMessageType() != null) {
            return sendMessage.getMessageType();
        }
        return ChatMessage.MessageType.TEXT;
    }

    private ChatMessageMedia resolveUploadedMedia(Long mediaId, ChatMessage.MessageType messageType) {
        if (mediaId == null) {
            throw new YHClientException("mediaId cannot be empty");
        }

        ChatMessageMedia media = chatMessageMediaMapper.selectUnboundById(mediaId);
        if (media == null) {
            throw new YHClientException("media does not exist or has already been used");
        }
        if (!media.getMediaType().equals(messageType.getCode())) {
            throw new YHClientException("media type mismatch");
        }
        if (!awsUtils.isObjectExist(media.getUrl())) {
            throw new YHClientException("media file has not been uploaded");
        }
        if (StringUtils.hasText(media.getCoverUrl()) && !awsUtils.isObjectExist(media.getCoverUrl())) {
            throw new YHClientException("media cover has not been uploaded");
        }
        return media;
    }

    private String getMediaMessageLabel(ChatMessage.MessageType messageType) {
        if (messageType == ChatMessage.MessageType.IMAGE) {
            return "[图片]";
        }
        return "[视频]";
    }

    private void sendAckMessage(ChatSendMessage sendMessage, Long senderId) {
        ChatACKMessage ackMessage = new ChatACKMessage();
        ackMessage.setConversationId(sendMessage.getConversationId());
        ackMessage.setClientMsgId(sendMessage.getClientMsgId());
        simpMessagingTemplate.convertAndSendToUser(senderId.toString(), "/queue/chat", ackMessage);
    }

    private Long updateReceiverUnreadCount(Long conversationId, ChatConversation conversation, ConversationUserNumber senderNumber) {
        Long receiverId = senderNumber.equals(ConversationUserNumber.USER1)
                ? conversation.getUser2Id()
                : conversation.getUser1Id();
        if (senderNumber.equals(ConversationUserNumber.USER1)) {
            chatConversationMapper.updateUser2UnreadCount(conversationId, receiverId);
        } else {
            chatConversationMapper.updateUser1UnreadCount(conversationId, receiverId);
        }
        return receiverId;
    }

    private void sendReceiveMessage(
            Long receiverId,
            Long senderId,
            Long conversationId,
            String content,
            ChatMessage.MessageType messageType,
            ChatMessageMedia media,
            LocalDateTime sentTime) {

        ChatReceiveMessage receiveMessage = new ChatReceiveMessage();
        receiveMessage.setConversationId(conversationId);
        receiveMessage.setSenderId(senderId);
        receiveMessage.setContent(content);
        receiveMessage.setMessageType(messageType);
        if (media != null) {
            receiveMessage.setMedia(chatMessageMediaService.toResponse(media));
        }
        receiveMessage.setSentTime(sentTime);

        simpMessagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/chat", receiveMessage);
    }
}
