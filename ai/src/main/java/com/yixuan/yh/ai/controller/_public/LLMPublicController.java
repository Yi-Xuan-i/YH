package com.yixuan.yh.ai.controller._public;

import com.yixuan.yh.ai.constant.LLMConstant;
import com.yixuan.yh.ai.service.LLMService;
import com.yixuan.yh.ai.utils.ReactiveUserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Tag(name = "LLMPublic")
@RestController
@RequestMapping("/public/llm")
public class LLMPublicController {

    @Autowired
    private LLMService llmService;

    @Operation(summary = "与LLM对话")
    @GetMapping("/chat/{conversationId}")
    public Flux<String> getLLMResponse(@PathVariable Long conversationId,
                                       @RequestParam @Size(max = LLMConstant.LLM_MSG_MAX_SIZE) String msg) {
        return ReactiveUserContext.getUserId()
                .flatMapMany(userId -> llmService.getLLMResponse(userId, conversationId, msg));
    }
}
