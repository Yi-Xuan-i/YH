package com.yixuan.yh.chat.websocket.controller;

import com.yixuan.yh.chat._enum.ConversationUserNumber;
import com.yixuan.yh.chat.entity.ChatMessage;
import com.yixuan.yh.chat.entity.ChatConversation;
import com.yixuan.yh.chat.mapper.ChatConversationMapper;
import com.yixuan.yh.chat.mapper.ChatMessageMapper;
import com.yixuan.yh.chat.websocket.pojo.ChatReceiveMessage;
import com.yixuan.yh.chat.websocket.pojo.ChatSendMessage;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class GlobalController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private ChatConversationMapper chatConversationMapper;

    @MessageMapping("/global/chat")
    public void handleChatMessage(
            ChatSendMessage sendMessage, @Header("simpSessionAttributes") Map<String, Object> attributes) {

        // 获取会话双方id
        ChatConversation conversation = chatConversationMapper.selectBothIdById(sendMessage.getConversationId());
        Long senderId = (Long) attributes.get("id");

        // 判断会话是否属于该用户（并获取“编号”）
        ConversationUserNumber senderNumber;
        if (senderId.equals(conversation.getUser1Id())) {
            senderNumber = ConversationUserNumber.USER1;
        } else if (senderId.equals(conversation.getUser2Id())) {
            senderNumber = ConversationUserNumber.USER2;
        } else {
            return;
        }

        // 信息入库
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(snowflakeUtils.nextId());
        chatMessage.setConversationId(sendMessage.getConversationId());
        chatMessage.setSenderId(senderId);
        chatMessage.setContent(sendMessage.getContent());
        chatMessage.setContentType(ChatMessage.ContentType.TEXT);
        chatMessage.setCreatedTime(LocalDateTime.now());

        chatMessageMapper.insert(chatMessage);

        // 更新未读数
        Long receiverId = senderNumber.equals(ConversationUserNumber.USER1) ? conversation.getUser2Id() : conversation.getUser1Id();
        if (senderNumber.equals(ConversationUserNumber.USER1)) {
            chatConversationMapper.updateUser2UnreadCount(sendMessage.getConversationId(), receiverId);
        } else {
            chatConversationMapper.updateUser1UnreadCount(sendMessage.getConversationId(), receiverId);
        }

        // 发送消息
        ChatReceiveMessage receiveMessage = new ChatReceiveMessage();
        receiveMessage.setSenderId(senderId);
        receiveMessage.setContent(sendMessage.getContent());
        receiveMessage.setSentTime(LocalDateTime.now());

        simpMessagingTemplate.convertAndSend("/queue/user" + "-" + receiverId, receiveMessage);
    }
}