package com.yixuan.study.microservices.user.service.impl;

import com.yixuan.study.microservices.user.entity.User;
import com.yixuan.study.microservices.user.mapper.UserMapper;
import com.yixuan.study.microservices.user.mapstruct.AuthMapStruct;
import com.yixuan.study.microservices.user.request.LoginRequest;
import com.yixuan.study.microservices.user.request.RegisterRequest;
import com.yixuan.study.microservices.user.service.AuthService;
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
    private UserMapper userMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Override
    public void register(RegisterRequest registerRequest) throws BadRequestException {
        /* 手机号唯一性检测 */
        if (userMapper.selectIsPhoneNumberExist(registerRequest.getPhoneNumber())) {
            throw new BadRequestException("手机号已经被注册！");
        }

        User user = AuthMapStruct.INSTANCE.registerRequestToUser(registerRequest);
        user.setId(snowflakeUtils.nextId());
        user.setName("路人甲");
        user.setEncodedPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userMapper.insertToRegister(user);
    }

    @Override
    public String login(LoginRequest loginRequest) throws BadRequestException {
        User user = userMapper.selectIdAndPassword(loginRequest.getPhoneNumber());

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getEncodedPassword())) {
            throw new BadRequestException("手机号或密码错误！"); // 如果用户不存在不应该直接返回“手机号错误！”，防止攻击者通过差异提示枚举有效用户。
        }

        // 生成令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        return jwtUtils.generateJwt(claims);
    }
}
