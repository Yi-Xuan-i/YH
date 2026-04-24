package com.yixuan.yh.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {
    private String address;
    private String password;
    private int connectionPoolSize = 6;
    private int connectionMinimumIdleSize = 2;
    private int pingConnectionInterval = 30000;
    private int timeout = 5000;
    private int idleConnectionTimeout = 60000;
}