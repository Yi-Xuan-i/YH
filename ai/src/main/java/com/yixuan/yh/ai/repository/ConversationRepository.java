package com.yixuan.yh.ai.repository;

import com.yixuan.yh.ai.entity.Conversation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationRepository extends R2dbcRepository<Conversation, Long> {
    Mono<Boolean> existsByConversationIdAndUserId(Long conversationId, Long userId);
    Flux<Conversation> findAllByUserIdOrderByConversationIdDesc(Long userId);
}
