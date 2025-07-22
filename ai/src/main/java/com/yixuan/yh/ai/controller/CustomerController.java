package com.yixuan.yh.ai.controller;

import com.yixuan.yh.ai.service.CustomerService;
import com.yixuan.yh.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "Customer")
@RestController
@RequestMapping("/me/customer-service")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "与智能客服聊天")
    @GetMapping
    public Mono<Result<String>> chat(@RequestParam String msg) {
        return customerService.chat(msg)
                .map(Result::success);
    }

}
