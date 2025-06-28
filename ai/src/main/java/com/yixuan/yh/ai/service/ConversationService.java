package com.yixuan.yh.ai.service;

import com.jayway.jsonpath.JsonPath;
import com.yixuan.yh.ai.response.ConversationMsgResponse;
import com.yixuan.yh.ai.response.ConversationResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ConversationService {
    Mono<Long> postConversation(Long userId);

    Mono<List<ConversationResponse>> getConversations(Long id);

    Mono<List<ConversationMsgResponse>> getConversationMsg(Long id, Long conversationId);
}
