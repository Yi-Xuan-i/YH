package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemoryServiceImpl implements MemoryService {

    private static final String SYSTEM_PROMPT = """
            # Role
            你是一个智能“用户长期记忆管理器”。你的职责是维护用户的长期记忆库。
            # Task & Workflow
            目前你只有一个任务，如果提到了长期重要信息，调用 `archival_memory_insert`。
            # Execution Rule
            直接根据你的推理结果，输出对应的工具调用（Tool Calls）。不要输出任何额外的废话、Markdown 标记或解释。
            # Current Conversation
            以下是当前用户输入的内容，请判断是否需要调用工具，不需要调用工具的话你返回个no即可：
            """;

    private final ChatClient memoryClient;


    @Override
    public void extractAndStoreMemory(Long userId, String msg) {
        memoryClient.prompt(SYSTEM_PROMPT + msg)
                .toolContext(Map.of("userId", userId))
                .call().content();
    }
}
