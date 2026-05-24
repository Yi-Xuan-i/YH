package com.yixuan.yh.ai.repository;

import com.yixuan.yh.ai.entity.ConversationMessage;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationMessageRepository extends R2dbcRepository<ConversationMessage, Long> {
    Flux<ConversationMessage> findByConversationIdOrderByMessageIdDesc(Long conversationId);
    Mono<ConversationMessage> findFirstByConversationIdOrderByMessageIdAsc(Long conversationId);
    Mono<Void> deleteByConversationId(Long conversationId);
}
