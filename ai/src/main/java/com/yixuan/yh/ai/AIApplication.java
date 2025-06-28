package com.yixuan.yh.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AIApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIApplication.class, args);
    }

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider toolProvider
    ) {
        return chatClientBuilder
                .defaultToolCallbacks(toolProvider)
                .build();
    }
}