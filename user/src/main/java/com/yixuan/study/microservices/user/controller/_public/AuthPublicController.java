package com.yixuan.study.microservices.user.controller._public;

import com.yixuan.study.microservices.user.request.LoginRequest;
import com.yixuan.study.microservices.user.request.RegisterRequest;
import com.yixuan.study.microservices.user.service.AuthService;
import com.yixuan.yh.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequestMapping("/public/auth")
public class AuthPublicController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Void> login(@Validated @RequestBody RegisterRequest registerRequest) throws BadRequestException {
        authService.register(registerRequest);
        return Result.successWithMsg("注册成功！");
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<String> login(@Validated @RequestBody LoginRequest loginRequest) throws BadRequestException {
        return Result.successWithMsg(authService.login(loginRequest), "登录成功！");
    }
}
