package com.yixuan.yh.ai.service;

import reactor.core.publisher.Flux;

public interface CustomerService {
    Flux<String> chat(Long userId, Long conversationId, String msg);
}
