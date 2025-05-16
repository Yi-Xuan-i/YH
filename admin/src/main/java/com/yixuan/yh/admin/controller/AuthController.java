package com.yixuan.yh.admin.controller;

import com.yixuan.yh.admin.request.LoginRequest;
import com.yixuan.yh.admin.service.AuthService;
import com.yixuan.yh.commom.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest loginRequest) throws BadRequestException {
        return Result.success(authService.login(loginRequest));
    }
}
