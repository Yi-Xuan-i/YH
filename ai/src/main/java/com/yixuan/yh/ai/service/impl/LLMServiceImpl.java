package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.cache.LlmSessionManager;
import com.yixuan.yh.ai.service.LLMService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class LLMServiceImpl extends AbstractConversationChatService implements LLMService {

    private static final String SYSTEM_PROMPT = """
            你是一个拥有高级内存管理的 AI 助手。你的上下文窗口容量是有限的。
            你的核心指令：
            如果用户提供的信息涉及重要的事实、知识或用户长期偏好，调用 archival_memory_insert 将其存入硬盘。
            如果用户问到过去的事情，而你不太了解并且 RAM（上下文）中也没有，你必须先调用 archival_memory_search 进行搜索，获取结果后再回答用户。
            """;
    private final LlmSessionManager llmSessionManager;

    public LLMServiceImpl(LlmSessionManager llmSessionManager) {
        super();
        this.llmSessionManager = llmSessionManager;
    }

    @Override
    public Flux<Object> getLLMResponse(Long userId, Long conversationId, String msg, boolean enableThinking) {
        return getHistoryMessages(userId, conversationId)
                .flatMapMany(historyMessages -> {
                    boolean hasMsg = msg != null && !msg.isEmpty();
                    if (!hasMsg && !llmSessionManager.hasSession(conversationId)) {
                        return Flux.empty();
                    }

                    // 1. 如果传了提示词，并且该会话当前没有生成任务，则启动后台大模型任务
                    if (msg != null && !msg.isEmpty() && !llmSessionManager.hasSession(conversationId)) {
                        String prompt = buildPrompt(SYSTEM_PROMPT, msg, historyMessages);
                        llmSessionManager.getOrCreateSink(conversationId);
                        startLlmGeneration(userId, conversationId, msg, prompt, enableThinking);
                    }

                    // 2. 将 Sink 转换为 Flux 并包装为 SSE 返回给前端
                    // 【特性表现】：
                    // - 如果是首次连接：随着大模型生成，字一个个蹦出来。
                    // - 如果是中途断线重连：由于 replay() 机制，之前生成好的所有 Token 会在连接建立的瞬间一次性全吐给前端，紧接着继续一个个蹦字。
                    return llmSessionManager.getStream(conversationId)
                            .map(token -> ServerSentEvent.<String>builder()
                                    .data(token)
                                    .build())
                            .doOnCancel(() -> {
                                // 当用户关闭网页或因网络断开时，WebFlux 会触发 cancel
                                // 但由于我们在 Service 层通过 .subscribe() 独立运行了流，
                                // 这里的 cancel 只会断开当前这个 SSE 连接，不会影响后台大模型的继续生成和缓存！
                                System.out.println("前端连接暂时断开，SessionId: " + conversationId);
                            });
                });
    }
}
