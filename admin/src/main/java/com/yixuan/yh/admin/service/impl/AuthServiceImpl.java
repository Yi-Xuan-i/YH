package com.yixuan.yh.admin.service.impl;

import com.yixuan.yh.admin.entity.Admin;
import com.yixuan.yh.admin.mapper.AdminMapper;
import com.yixuan.yh.admin.request.LoginRequest;
import com.yixuan.yh.admin.service.AuthService;
import com.yixuan.yh.common.utils.JwtUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String login(LoginRequest loginRequest) throws BadRequestException {
        Admin admin = adminMapper.selectIdAndPassword(loginRequest.getName());
        if (admin == null || !passwordEncoder.matches(loginRequest.getPassword(), admin.getEncodedPassword())) {
            throw new BadRequestException("用户名或密码错误！");
        }

        // 生成令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", admin.getId());
        return jwtUtils.generateJwt(claims);
    }
}
