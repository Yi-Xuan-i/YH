package com.yixuan.yh.ai.controller;

import com.yixuan.yh.ai.service.LLMService;
import com.yixuan.yh.ai.utils.ReactiveUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/public/llm")
public class LLMController {

    @Autowired
    private LLMService llmService;

    @GetMapping("/chat/{conversationId}")
    public Flux<String> getLLMResponse(@PathVariable Long conversationId, @RequestParam String msg) {
        return ReactiveUserContext.getUserId()
                .flatMapMany(userId -> llmService.getLLMResponse(userId, conversationId, msg));
    }
}
