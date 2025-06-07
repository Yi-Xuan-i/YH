package com.yixuan.yh.gateway.filter;

import com.yixuan.yh.gateway.properties.AuthProperties;
import com.yixuan.yh.gateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthProperties authProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders requestHeaders = request.getHeaders();
        HttpHeaders responseHeaders = response.getHeaders();

        if (isExcludedPath(request.getPath().value())) {
            return chain.filter(exchange);
        }

        // 拦截 WebSocket 握手请求
        if (exchange.getRequest().getHeaders().containsKey("Upgrade")) {
            String token = exchange.getRequest().getQueryParams().getFirst("token");
            Claims claims;
            if ((claims = parseToken(token)) == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            Long id = (Long) claims.get("id");
            exchange = exchange.mutate().
                    request(builder -> builder.header("id", id.toString())).build();
            return chain.filter(exchange);
        }

        String token = requestHeaders.getFirst(HttpHeaders.AUTHORIZATION);
        Claims claims;
        // 认证令牌失败
        if (
                token == null ||
                !token.startsWith("Bearer ") ||
                (claims = parseToken(token)) == null
        ) {
            // 设置状态码和响应类型
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            // 构建响应体
            String responseBody = "{\"code\":\"0\",\"msg\":\"未登录！\"}";
            DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
            // 返回响应
            return response.writeWith(Mono.just(buffer));
        }

        // 取出令牌的用户信息, 携带在请求头传递给其它微服务。
        Long id = (Long) claims.get("id");
        exchange = exchange.mutate().
                request(builder -> builder.header("id", id.toString())).build();

        return chain.filter(exchange);
    }

    private boolean isExcludedPath(String path) {
        for (String pattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private Claims parseToken(String token) {
        try {
            return jwtUtils.parseJwt(token.substring(7));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
