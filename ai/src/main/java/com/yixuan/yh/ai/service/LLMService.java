package com.yixuan.yh.ai.service;

import reactor.core.publisher.Flux;

public interface LLMService {
    Flux<String> getLLMResponse(Long userId, Long conversationId, String msg);
}
