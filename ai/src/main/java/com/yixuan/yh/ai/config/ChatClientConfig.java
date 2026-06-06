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
    public ToolCallbackProvider filteredToolProvider(
            ToolCallbackProvider delegate) {
        return () -> Arrays.stream(delegate.getToolCallbacks())
                .filter(tool -> tool.getToolDefinition().name().contains("memory"))
                .toArray(ToolCallback[]::new);
    }

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider filteredToolProvider) {
        return chatClientBuilder
                .defaultToolCallbacks(filteredToolProvider)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
