package com.yixuan.yh.chat.websocket.controller;

import com.yixuan.yh.chat.service.ChatMessageService;
import com.yixuan.yh.chat.websocket.pojo.ChatSendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GlobalController {

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/global/chat")
    public void handleChatMessage(ChatSendMessage sendMessage, Principal principal) {
        chatMessageService.handleChatMessage(sendMessage, Long.parseLong(principal.getName()));
    }
}
