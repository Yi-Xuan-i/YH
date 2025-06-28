package com.yixuan.yh.ai.cache;

import com.yixuan.yh.ai.repository.IntentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class IntentResponseCache {

    @Autowired
    private IntentRepository intentRepository;

    public Mono<String> getResponse(String intent) {
        return intentRepository.findByIntentRandomly(intent);
    }
}
