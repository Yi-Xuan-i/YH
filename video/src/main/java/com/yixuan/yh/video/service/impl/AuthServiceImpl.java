package com.yixuan.yh.video.service.impl;

import com.yixuan.yh.video.entity.User;
import com.yixuan.yh.video.mapper.AuthMapper;
import com.yixuan.yh.video.mapstruct.UserMapStruct;
import com.yixuan.yh.video.request.LoginRequest;
import com.yixuan.yh.video.request.RegisterRequest;
import com.yixuan.yh.video.service.AuthService;
import com.yixuan.yh.common.utils.JwtUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
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
    private AuthMapper authMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Override
    public void register(RegisterRequest registerRequest) {
        User user = UserMapStruct.INSTANCE.registerRequestToUser(registerRequest);
        user.setId(snowflakeUtils.nextId());
        user.setEncodedPassword(passwordEncoder.encode(registerRequest.getPassword()));
        authMapper.insertToRegister(user);
    }

    @Override
    public String login(LoginRequest loginRequest) throws BadRequestException {
        User user = authMapper.selectIdAndPassword(loginRequest.getPhoneNumber());

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getEncodedPassword())) {
            throw new BadRequestException("手机号或密码错误!"); // 如果用户不存在不应该直接返回“手机号错误！”，防止攻击者通过差异提示枚举有效用户。
        }

        // 生成令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        return jwtUtils.generateJwt(claims);
    }
}
