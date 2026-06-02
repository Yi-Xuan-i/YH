package com.yixuan.yh.mcp.server;

import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpMeta;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserService {

    private final WebClient webClient;

    @McpTool(name = "获取当前用户的名称", description = "可以获取当前用户的名称")
    public Mono<String> getUsername(McpMeta mcpMeta) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("userService")
                        .path("/user/api/private/name")
                        .queryParam("id", mcpMeta.get("userId"))
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

}
