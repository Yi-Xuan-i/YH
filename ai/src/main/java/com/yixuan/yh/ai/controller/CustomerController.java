package com.yixuan.yh.ai.controller;

import com.yixuan.yh.ai.service.CustomerService;
import com.yixuan.yh.common.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/me/customer-service")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Mono<Result<String>> chat(@RequestParam String msg) {
        return customerService.chat(msg)
                .map(Result::success);
    }

}
