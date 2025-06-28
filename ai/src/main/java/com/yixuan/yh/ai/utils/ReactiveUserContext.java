package com.yixuan.yh.ai.utils;

import reactor.core.publisher.Mono;

public class ReactiveUserContext {
    public static final String CONTEXT_KEY = "userId";

    public static Mono<Long> getUserId() {
        return Mono.deferContextual(ctx ->
                Mono.justOrEmpty(ctx.getOrEmpty(CONTEXT_KEY))
        );
    }
}