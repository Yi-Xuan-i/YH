package com.yixuan.yh.admin.interceptor;

import com.yixuan.yh.common.utils.JwtUtils;
import com.yixuan.yh.common.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthNInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        Claims claims;
        try {
            claims = jwtUtils.parseJwt(token.substring(7));
        } catch (Exception e) {
            return false;
        }
        Object id = claims.get("id");
        if (id instanceof Integer) {
            UserContext.setUser(Long.valueOf((Integer) id));
        } else if (id instanceof Long) {
            UserContext.setUser((Long) id);
        } else {
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}
