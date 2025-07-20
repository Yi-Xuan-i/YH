package com.yixuan.yh.chat.controller;

import com.yixuan.yh.chat.response.ConversationMessageResponse;
import com.yixuan.yh.chat.response.RecentContactResponse;
import com.yixuan.yh.chat.service.ConversationService;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/recent-contacts")
    public Result<List<RecentContactResponse>> getRecentContacts() {
        return Result.success(conversationService.getRecentContacts(UserContext.getUser()));
    }

    @GetMapping("/messages/{conversationId}")
    public Result<List<ConversationMessageResponse>> getConversationMessages(@PathVariable Long conversationId) throws BadRequestException {
        return Result.success(conversationService.getConversationMessages(UserContext.getUser(), conversationId));
    }

}
