package com.yixuan.yh.chat.service;

import com.yixuan.yh.chat.websocket.pojo.ChatSendMessage;

public interface ChatMessageService {
    void handleChatMessage(ChatSendMessage sendMessage, Long senderId);
}
