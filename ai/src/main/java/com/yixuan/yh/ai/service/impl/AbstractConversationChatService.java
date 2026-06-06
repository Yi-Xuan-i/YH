package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.cache.ConversationMessageCache;
import com.yixuan.yh.ai.cache.LlmSessionManager;
import com.yixuan.yh.ai.entity.ConversationMessage;
import com.yixuan.yh.ai.repository.ConversationRepository;
import com.yixuan.yh.common.exception.YHClientException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractConversationChatService {

    private static final String DONE_MESSAGE = "[DONE]";
    private static final String EMPTY_RESPONSE_MESSAGE = "[ERROR]EMPTY_LLM_RESPONSE";
    private static final String THINK_START_TAG = "<think>";
    private static final String THINK_END_TAG = "</think>";

    @Autowired
    protected ChatClient chatClient;
    @Autowired
    private ConversationMessageCache conversationMessageCache;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private LlmSessionManager llmSessionManager;

    protected Mono<List<ConversationMessage>> getHistoryMessages(Long userId, Long conversationId) {
        return conversationRepository.existsByConversationIdAndUserId(conversationId, userId)
                .flatMap(exist -> {
                    if (!exist) {
                        return Mono.error(new YHClientException("会话异常"));
                    }
                    return conversationMessageCache.getMessage(conversationId).collectList();
                });
    }

    protected String buildPrompt(String systemPrompt, String msg, List<ConversationMessage> historyMessages) {
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("system: ").append(systemPrompt);
        if (!systemPrompt.endsWith("\n")) {
            contextBuilder.append("\n");
        }
        for (int i = historyMessages.size() - 1; i >= 0; i--) {
            ConversationMessage message = historyMessages.get(i);
            contextBuilder.append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");
        }
        contextBuilder.append("user: ").append(msg).append("\n");
        return contextBuilder.toString();
    }

    public void startLlmGeneration(Long userId, Long conversationId, String msg,
                                   String prompt, boolean enableThinking) {
        Sinks.Many<String> sink = llmSessionManager.getOrCreateSink(conversationId);

        // 【核心】在后台异步订阅大模型的流，与前端 HTTP 请求的生命周期完全脱钩！
        streamChatResponse(userId, conversationId, msg, prompt, enableThinking)
                .subscribe(
                        token -> {
                            // 生产者：源源不断地往 Sink 缓冲区里塞数据
                            sink.tryEmitNext(token.toString());
                        },
                        error -> {
                            sink.tryEmitError(error);
                            llmSessionManager.removeSession(conversationId);
                        },
                        () -> {
                            // 生成完毕，关闭 Sink，并从内存中移除会话记录
                            sink.tryEmitComplete();
                            llmSessionManager.removeSession(conversationId);
                        }
                );
    }

    protected Flux<Object> streamChatResponse(Long userId, Long conversationId, String msg,
                                              String prompt, boolean enableThinking) {
        StringBuilder fullResponse = new StringBuilder();
        AtomicBoolean hasOutput = new AtomicBoolean(false);
        AtomicBoolean thinkingOpened = new AtomicBoolean(false);

        return saveUserMessage(conversationId, msg)
                .thenMany(Flux.defer(() ->
                                chatClient.prompt(prompt)
                                        .toolContext(Map.of("userId", userId))
                                        .options(OpenAiChatOptions.builder()
                                                .extraBody(Map.of("enable_thinking", enableThinking))
                                                .build())
                                        .stream()
                                        .chatResponse()
                                        .concatMap(response -> parseResponse(response, hasOutput, thinkingOpened))
                                        .concatWith(Flux.defer(() -> buildEndMessage(hasOutput, thinkingOpened)))
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(chunk -> {
                    if (!DONE_MESSAGE.equals(chunk) && !EMPTY_RESPONSE_MESSAGE.equals(chunk)) {
                        fullResponse.append(chunk);
                    }
                })
                .concatWith(Mono.defer(() ->
                        saveAssistantMessageIfPresent(conversationId, fullResponse.toString())
                                .then(Mono.<String>empty())
                ));
    }

    private Flux<Object> parseResponse(Object response, AtomicBoolean hasOutput,
                                       AtomicBoolean thinkingOpened) {
        List<Object> chunks = new ArrayList<>();
        if (!(response instanceof org.springframework.ai.chat.model.ChatResponse chatResponse)
                || chatResponse.getResult() == null
                || chatResponse.getResult().getOutput() == null) {
            return Flux.fromIterable(chunks);
        }

        var output = chatResponse.getResult().getOutput();
        Object reasoningObj = getMetadataValue(output.getMetadata(),
                "reasoningContent",
                "reasoning_content",
                "reasoning");
        if (reasoningObj != null) {
            String reasoning = reasoningObj.toString();
            if (!reasoning.isBlank()) {
                hasOutput.set(true);
                if (thinkingOpened.compareAndSet(false, true)) {
                    chunks.add(THINK_START_TAG);
                }
                chunks.add(reasoning);
            }
        }

        String text = output.getText();
        if (text != null && !text.isBlank()) {
            hasOutput.set(true);
            if (thinkingOpened.compareAndSet(true, false)) {
                chunks.add(THINK_END_TAG);
            }
            chunks.add(text);
        }
        return Flux.fromIterable(chunks);
    }

    private Flux<Object> buildEndMessage(AtomicBoolean hasOutput, AtomicBoolean thinkingOpened) {
        if (!hasOutput.get()) {
            return Flux.just(EMPTY_RESPONSE_MESSAGE, DONE_MESSAGE);
        }
        if (thinkingOpened.compareAndSet(true, false)) {
            return Flux.just(THINK_END_TAG, DONE_MESSAGE);
        }
        return Flux.just(DONE_MESSAGE);
    }

    private Mono<Void> saveUserMessage(Long conversationId, String msg) {
        if (msg == null || msg.isEmpty()) {
            return Mono.empty();
        }
        return conversationMessageCache.addUserMessage(conversationId, msg).then();
    }

    private Mono<Void> saveAssistantMessageIfPresent(Long conversationId, String response) {
        if (response == null || response.isEmpty()) {
            return Mono.empty();
        }
        return conversationMessageCache.addAssistantMessage(conversationId, response).then();
    }

    private Object getMetadataValue(Map<String, Object> metadata, String... keys) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            Object value = metadata.get(key);
            if (Objects.nonNull(value)) {
                return value;
            }
        }
        return null;
    }
}
