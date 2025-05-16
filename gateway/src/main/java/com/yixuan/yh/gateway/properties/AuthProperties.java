package com.yixuan.yh.gateway.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "gateway.auth")
public class AuthProperties {
    private List<String> excludePaths; // 排除的路径
}
