package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.cache.ConversationMessageCache;
import com.yixuan.yh.ai.cache.IntentResponseCache;
import com.yixuan.yh.ai.constant.RedisKeyConstant;
import com.yixuan.yh.ai.entity.ConversationMessage;
import com.yixuan.yh.ai.repository.ConversationRepository;
import com.yixuan.yh.ai.service.CustomerService;
import com.yixuan.yh.ai.utils.ReactiveUserContext;
import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.response.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ConversationMessageCache conversationMessageCache;
    @Autowired
    private ConversationRepository conversationRepository;

    @Override
    public Flux<String> chat(Long userId, Long conversationId, String msg) {
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
                                        contextBuilder.append("system： 你是一个商城智能客服。\n");
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

    enum ChatStatus {
        NORMAL(0),
        RETURN_GOODS(1),
        RETURN_GOODS_CONFIRM(2);

        private final int value;

        ChatStatus(int i) {
            value = i;
        }

        public static ChatStatus fromValue(int value) {
            for (ChatStatus status : ChatStatus.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("无效的 ChatStatus value: " + value);
        }

        public static ChatStatus fromStringValue(String value) {
            return fromValue(Integer.parseInt(value));
        }

        public int getValue() {
            return value;
        }

        public String getStringValue() {
            return Integer.toString(value);
        }
    }
}
