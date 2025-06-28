package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.entity.Conversation;
import com.yixuan.yh.ai.repository.ConversationMessageRepository;
import com.yixuan.yh.ai.repository.ConversationRepository;
import com.yixuan.yh.ai.response.ConversationMsgResponse;
import com.yixuan.yh.ai.response.ConversationResponse;
import com.yixuan.yh.ai.service.ConversationService;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private ConversationMessageRepository conversationMessageRepository;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Override
    public Mono<Long> postConversation(Long userId) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(snowflakeUtils.nextId());
        conversation.setUserId(userId);
        conversation.setTitle("对话");
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setCreatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation)
                .map(Conversation::getConversationId);
    }

    @Override
    public Mono<List<ConversationResponse>> getConversations(Long id) {
        return conversationRepository.findAllByUserIdOrderByConversationIdDesc(id)
                .map(conversation -> {
                    ConversationResponse conversationResponse = new ConversationResponse();
                    conversationResponse.setConversationId(conversation.getConversationId());
                    conversationResponse.setTitle(conversation.getTitle());
                    conversationResponse.setUpdatedAt(conversation.getUpdatedAt());
                    return conversationResponse;
                })
                .collectList();
    }

    @Override
    public Mono<List<ConversationMsgResponse>> getConversationMsg(Long id, Long conversationId) {
        return conversationRepository.existsByConversationIdAndUserId(conversationId, id)
                .flatMap(exist -> {
                    if (!exist) {
                        return Mono.error(new HttpException("异常！"));
                    }
                    return conversationMessageRepository.findByConversationIdOrderByMessageIdDesc(conversationId)
                            .map(conversationMessage -> {
                                ConversationMsgResponse conversationMsgResponse = new ConversationMsgResponse();
                                conversationMsgResponse.setRole(conversationMessage.getRole());
                                conversationMsgResponse.setContent(conversationMessage.getContent());
                                return conversationMsgResponse;
                            })
                            .collectList();
                });
    }
}
