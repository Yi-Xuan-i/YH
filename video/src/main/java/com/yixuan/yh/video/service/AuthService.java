package com.yixuan.yh.video.service;

import com.yixuan.yh.video.request.LoginRequest;
import com.yixuan.yh.video.request.RegisterRequest;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    String login(LoginRequest loginRequest) throws BadRequestException;
}
