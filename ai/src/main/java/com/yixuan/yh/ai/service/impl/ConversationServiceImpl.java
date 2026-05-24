package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.entity.Conversation;
import com.yixuan.yh.ai.entity.ConversationMessage;
import com.yixuan.yh.ai.repository.ConversationMessageRepository;
import com.yixuan.yh.ai.repository.ConversationRepository;
import com.yixuan.yh.ai.response.ConversationMsgResponse;
import com.yixuan.yh.ai.response.ConversationResponse;
import com.yixuan.yh.ai.service.ConversationService;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import org.apache.http.HttpException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private ConversationMessageRepository conversationMessageRepository;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private ChatClient chatClient;

    @Override
    public Mono<String> postConversation(Long userId) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(snowflakeUtils.nextId());
        conversation.setUserId(userId);
        conversation.setTitle("对话");
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setCreatedAt(LocalDateTime.now());
        return r2dbcEntityTemplate.insert(conversation)
                .map(Conversation::getConversationId)
                .map(id -> Long.toString(id));
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

    @Override
    public Mono<String> deleteConversation(Long id, Long conversationId) {
        return conversationRepository.existsByConversationIdAndUserId(conversationId, id)
                .flatMap(exist -> {
                    if (!exist) {
                        return Mono.error(new HttpException("会话异常！"));
                    }
                    return conversationMessageRepository.deleteByConversationId(conversationId)
                            .then(conversationRepository.deleteById(conversationId))
                            .thenReturn("删除成功");
                });
    }

    @Override
    public Mono<String> generateConversationTitle(Long id, Long conversationId) {
        return conversationMessageRepository.findFirstByConversationIdOrderByMessageIdAsc(conversationId)
                .map(ConversationMessage::getContent)
                .flatMap(content ->
                        chatClient.prompt("""
                                        system:
                                        角色：你是一个精炼、高效的文本处理助手。
                                        任务：我接下来提供的一段对话/文本，并为其生成一个简短、精准、有辨识度的会话标题。
                                        生成规则：
                                        字数限制：最多不超过 15 个字。
                                        核心原则：不废话，直奔主题，能够一眼看出这篇对话的核心讨论点。
                                        格式要求：不要包含任何前缀（如“标题：”、“会话名：”），不要加引号，不要解释原因，直接输出最终的标题文字。
                                        语言：与对话所使用的主要语言保持一致。
                                        特别注意：你不是负责回答问题，而是负责生成标题！！！
                                        下面是内容：
                                        """)
                                .user(content)
                                .stream()
                                .content()
                                .collectList()
                                .map(list -> String.join("", list))
                )
                .flatMap(title ->
                        conversationRepository.findById(conversationId)
                                .flatMap(conversation -> {
                                    conversation.setTitle(title);
                                    return conversationRepository.save(conversation);
                                })
                                .map(savedConversation -> title)
                );
    }
}
