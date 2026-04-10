package com.yixuan.yh.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.yixuan.yh.user.feign"})
public class MCPServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MCPServerApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public HttpMessageConverters messageConverters(ObjectMapper objectMapper) {
        return new HttpMessageConverters(
                new MappingJackson2HttpMessageConverter(objectMapper)
        );
    }
}