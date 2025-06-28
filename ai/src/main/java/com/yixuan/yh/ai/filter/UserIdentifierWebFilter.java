package com.yixuan.yh.ai.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserIdentifierWebFilter implements WebFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ObjectMapper objectMapper;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (    antPathMatcher.match("/ai/api/public/ws/**", exchange.getRequest().getPath().value()) ||
                antPathMatcher.match("/ai/api/public/llm/chat/*", exchange.getRequest().getPath().value())) {
            String token = exchange.getRequest().getQueryParams().getFirst("token");
            try {
                Claims claims = jwtUtils.parseJwt(token);
                System.out.println(claims);
                return chain.filter(exchange)
                        .contextWrite(ctx -> ctx.put("userId", claims.get("id")));
            } catch (Exception e) {
                try {
                    return handleJwtError(exchange);
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        // 从请求头获取ID
        String idString = exchange.getRequest().getHeaders().getFirst("id");

        if (idString != null) {
            Long id = Long.valueOf(idString);
            // 将ID存储到响应式上下文
            return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put("userId", id));
        }

        return chain.filter(exchange);
    }

    private Mono<Void> handleJwtError(ServerWebExchange exchange) throws JsonProcessingException {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 构建错误响应体
        Map<String, String> errorBody = Map.of(
                "status", "401",
                "message", "未登录！",
                "path", exchange.getRequest().getPath().value()
        );

        // 序列化为 JSON 并写入响应
        byte[] bytes = objectMapper.writeValueAsBytes(errorBody);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}