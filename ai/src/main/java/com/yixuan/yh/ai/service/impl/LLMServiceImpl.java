package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.cache.ConversationMessageCache;
import com.yixuan.yh.ai.entity.ConversationMessage;
import com.yixuan.yh.ai.repository.ConversationRepository;
import com.yixuan.yh.ai.service.LLMService;
import com.yixuan.yh.common.exception.YHClientException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

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
                                return Flux.error(new YHClientException("会话异常！"));
                            }
                            return conversationMessageCache.getMessage(conversationId)
                                    .collectList()
                                    .flatMapMany(historyMessages -> {
                                        // 构建历史上下文
                                        StringBuilder contextBuilder = new StringBuilder();
                                        contextBuilder.append("system: ");
                                        contextBuilder.append("""
                                                你是一个拥有高级内存管理的 AI 助手。你的上下文窗口容量是有限的。
                                                你的核心指令：
                                                如果用户提供的信息涉及重要的事实、知识或用户长期偏好，调用 archival_memory_insert 将其存入硬盘。
                                                如果用户问到过去的事情，而你不太了解并且RAM（上下文）中也没有，你必须先调用 archival_memory_search 进行搜索，获取结果后再回答用户。""");
                                        for (int i = historyMessages.size() - 1; i >= 0; i--) {
                                            ConversationMessage message = historyMessages.get(i);
                                            contextBuilder.append(message.getRole())
                                                    .append(": ")
                                                    .append(message.getContent())
                                                    .append("\n");
                                        }
                                        contextBuilder.append("user: ").append(msg).append("\n");

                                        StringBuilder fullResponse = new StringBuilder();
                                        return Mono.fromCallable(() ->
                                                        chatClient.prompt(contextBuilder.toString())
                                                                .toolContext(Map.of("userId", userId))
                                                                .stream()
                                                                .content()
                                                )
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .flatMapMany(stream -> stream
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
                                                        )
                                                );
                                    });
                        }
                );
    }
}