package com.yixuan.yh.admin.config;

import com.yixuan.yh.admin.interceptor.AdminAuthNInterceptor;
import com.yixuan.yh.admin.interceptor.AuditInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAuthNInterceptor adminAuthNInterceptor;
    @Autowired
    private AuditInterceptor auditInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthNInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(auditInterceptor).addPathPatterns("/api/**");
    }
}
