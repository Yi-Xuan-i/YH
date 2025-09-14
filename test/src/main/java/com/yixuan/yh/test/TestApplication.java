package com.yixuan.yh.test;

import com.yixuan.yh.test.mcp.CommonService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider commonTools(CommonService commonService) {
        return MethodToolCallbackProvider.builder().toolObjects(commonService).build();
    }
}