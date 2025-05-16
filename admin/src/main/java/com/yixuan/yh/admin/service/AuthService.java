package com.yixuan.yh.admin.service;

import com.yixuan.yh.admin.request.LoginRequest;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    String login(LoginRequest loginRequest) throws BadRequestException;
}
