package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.cache.ConversationMessageCache;
import com.yixuan.yh.ai.entity.ConversationMessage;
import com.yixuan.yh.ai.repository.ConversationRepository;
import com.yixuan.yh.ai.service.LLMService;
import org.apache.http.HttpException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class LLMServiceImpl implements LLMService {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ConversationMessageCache conversationMessageCache;
    @Autowired
    private ConversationRepository conversationRepository;

    @Override
    public Flux<String> getLLMResponse(Long userId, Long conversationId, String msg) {
        return conversationRepository.existsByConversationIdAndUserId(conversationId, userId)
                .flatMapMany(
                        exist -> {
                            if (!exist) {
                                return Flux.error(new HttpException("异常！"));
                            }
                            return conversationMessageCache.getMessage(conversationId)
                                    .collectList()
                                    .publishOn(Schedulers.boundedElastic())
                                    .flatMapMany(historyMessages -> {
                                        // 构建历史上下文
                                        StringBuilder contextBuilder = new StringBuilder();
                                        for (int i = historyMessages.size() - 1; i >= 0; i--) {
                                            ConversationMessage message = historyMessages.get(i);
                                            contextBuilder.append(message.getRole())
                                                    .append(": ")
                                                    .append(message.getContent())
                                                    .append("\n");
                                        }
                                        contextBuilder.append("user: ").append(msg).append("\n");

                                        StringBuilder fullResponse = new StringBuilder();
                                        return chatClient.prompt(contextBuilder.toString())
                                                .stream()
                                                .content()
                                                .map(text -> {
                                                    fullResponse.append(text);
                                                    return text;
                                                })
                                                .concatWith(
                                                        Mono.defer(() ->
                                                                conversationMessageCache.addMessage(
                                                                                conversationId,
                                                                                List.of(msg, fullResponse.toString())
                                                                        )
                                                                        .then(Mono.empty())
                                                        )
                                                );
                                    });
                        }
                );
    }
}