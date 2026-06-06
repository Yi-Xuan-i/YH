package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.cache.LlmSessionManager;
import com.yixuan.yh.ai.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LLMServiceImpl extends AbstractConversationChatService implements LLMService {

    private static final String SYSTEM_PROMPT = """
            # Role
            你是一个极具共情力、聪明的 AI 个人专属助手。你与用户拥有长期的合作与信任关系。

            # Memory Access Capabilities (记忆检索能力说明)
            为了能够像老朋友一样了解用户，你被赋予了访问用户“长期记忆数据库”的权限（调用archival_memory_search）。
            1. **主动检索意识**：当用户的提问涉及到过去的事件、特定的人物、未明说的爱好、历史项目，或者使用了模糊指代（如“上次那个事”、“我女朋友”、“老地方”）时，你**必须首先调用** `query_user_memory` 工具来获取背景信息。
            2. **严禁盲目猜测**：如果缺乏足够的背景信息来回答用户的问题，不要编造，立即去查记忆。
            3. **无需检索的情况**：如果用户只是在进行通用的常识问答（如“1+1等于几”）、当下情绪的宣泄、或者当前的闲聊，则**不要**调用记忆工具，直接回答即可。

            # Conversational Guidelines
            - 拿到工具返回的记忆后，请自然地将其融入到你的回复中，不要机械地罗列。
            - 如果调用工具后发现记忆库为空，或者找不到相关内容，请礼貌地向用户确认（例如：“关于上次的项目，我这里一时没找到记录，你能提醒我一下它的名字吗？”）。

            # Current Conversation
            以下是当前进行中的对话，请判断是否需要调用工具，或者直接给出回复：
            """;
    private final LlmSessionManager llmSessionManager;

    @Override
    public Flux<Object> getLLMResponse(Long userId, Long conversationId, String msg, boolean enableThinking) {
        return getHistoryMessages(userId, conversationId)
                .flatMapMany(historyMessages -> {
                    boolean hasMsg = msg != null && !msg.isEmpty();
                    if (!hasMsg && !llmSessionManager.hasSession(conversationId)) {
                        return Flux.empty();
                    }

                    if (msg != null && !msg.isEmpty() && !llmSessionManager.hasSession(conversationId)) {
                        String prompt = buildPrompt(SYSTEM_PROMPT, msg, historyMessages);
                        llmSessionManager.getOrCreateSink(conversationId);
                        startLlmGeneration(userId, conversationId, msg, prompt, enableThinking);
                    }

                    return llmSessionManager.getStream(conversationId)
                            .map(token -> ServerSentEvent.<String>builder()
                                    .data(token)
                                    .build());
                });
    }
}
