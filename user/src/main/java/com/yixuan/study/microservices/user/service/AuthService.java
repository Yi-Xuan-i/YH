package com.yixuan.study.microservices.user.service;

import com.yixuan.study.microservices.user.request.LoginRequest;
import com.yixuan.study.microservices.user.request.RegisterRequest;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    void register(RegisterRequest registerRequest) throws BadRequestException;
    String login(LoginRequest loginRequest) throws BadRequestException;
}
