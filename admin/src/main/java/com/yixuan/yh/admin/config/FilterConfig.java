package com.yixuan.yh.admin.config;

import com.yixuan.yh.admin.filter.CachingRequestBodyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private CachingRequestBodyFilter cachingRequestBodyFilter;

    @Bean
    public FilterRegistrationBean<CachingRequestBodyFilter> filterRegistrationBean() {
        FilterRegistrationBean<CachingRequestBodyFilter> filterRegistrationBean = new FilterRegistrationBean<>(cachingRequestBodyFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
