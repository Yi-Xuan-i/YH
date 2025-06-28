package com.yixuan.yh.ai.service;

import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<String> chat(String msg);
}
