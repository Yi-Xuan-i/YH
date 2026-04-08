package com.yixuan.yh.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider toolProvider
    ) {
        return chatClientBuilder
                .defaultToolCallbacks(toolProvider)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
