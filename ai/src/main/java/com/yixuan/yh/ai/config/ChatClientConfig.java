package com.yixuan.yh.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider toolCallbackProvider) {
        ToolCallback[] toolCallbacks = Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .filter(tool -> tool.getToolDefinition().name().equals("archival_memory_search"))
                .toArray(ToolCallback[]::new);
        return chatClientBuilder
                .defaultToolCallbacks(toolCallbacks)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @Bean
    public ChatClient memoryClient(
            ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider toolCallbackProvider) {
        ToolCallback[] toolCallbacks = Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .filter(tool -> tool.getToolDefinition().name().equals("archival_memory_insert"))
                .toArray(ToolCallback[]::new);
        return chatClientBuilder
                .defaultToolCallbacks(toolCallbacks)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
