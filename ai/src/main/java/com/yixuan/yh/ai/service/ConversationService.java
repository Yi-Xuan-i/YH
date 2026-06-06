package com.yixuan.yh.ai.service;

import com.yixuan.yh.ai.response.ConversationMsgResponse;
import com.yixuan.yh.ai.response.ConversationResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ConversationService {
    Mono<String> postConversation(Long userId);

    Mono<List<ConversationResponse>> getConversations(Long id);

    Mono<List<ConversationMsgResponse>> getConversationMsg(Long id, Long conversationId);

    Mono<String> deleteConversation(Long id, Long conversationId);

    Mono<String> generateConversationTitle(Long userId, Long conversationId);
}
