package com.yixuan.yh.chat.controller;

import com.yixuan.yh.chat.response.ConversationMessageResponse;
import com.yixuan.yh.chat.response.RecentContactResponse;
import com.yixuan.yh.chat.service.ConversationService;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Conversation")
@RestController
@RequestMapping("/me/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Operation(summary = "获取最近联系人（会话）")
    @GetMapping("/recent-contacts")
    public Result<List<RecentContactResponse>> getRecentContacts() {
        return Result.success(conversationService.getRecentContacts(UserContext.getUser()));
    }

    @Operation(summary = "获取会话信息")
    @GetMapping("/messages/{conversationId}")
    public Result<List<ConversationMessageResponse>> getConversationMessages(@PathVariable Long conversationId, @RequestParam(required = false) Long lastMinId) throws BadRequestException {
        return Result.success(conversationService.getConversationMessages(UserContext.getUser(), conversationId, lastMinId));
    }

}
