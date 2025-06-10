package com.yixuan.yh.user.service;

import com.yixuan.yh.user.pojo.request.LoginRequest;
import com.yixuan.yh.user.pojo.request.RegisterRequest;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    void register(RegisterRequest registerRequest) throws BadRequestException;
    String login(LoginRequest loginRequest) throws BadRequestException;
}
