package com.yixuan.yh.mcp;

import com.yixuan.yh.mcp.server.CustomerService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MCPServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MCPServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider commonTools(CustomerService customerService) {
        return MethodToolCallbackProvider.builder().toolObjects(customerService).build();
    }
}