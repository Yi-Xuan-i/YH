package com.yixuan.yh.ai.controller;

import com.yixuan.yh.ai.response.ConversationMsgResponse;
import com.yixuan.yh.ai.response.ConversationResponse;
import com.yixuan.yh.ai.service.ConversationService;
import com.yixuan.yh.ai.utils.ReactiveUserContext;
import com.yixuan.yh.common.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/me/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    Mono<Result<Long>> postConversation() {
        return ReactiveUserContext.getUserId().flatMap(id -> conversationService.postConversation(id)
                .map(Result::success));
    }

    @GetMapping("/list")
    Mono<Result<List<ConversationResponse>>> getConversations() {
        return ReactiveUserContext.getUserId().flatMap(id -> conversationService.getConversations(id)
                .map(Result::success));
    }

    @GetMapping("/msg/{conversationId}")
    Mono<Result<List<ConversationMsgResponse>>> getConversationMsg(@PathVariable Long conversationId) {
        return ReactiveUserContext.getUserId().flatMap(id -> conversationService.getConversationMsg(id, conversationId)
                .map(Result::success));
    }

}
