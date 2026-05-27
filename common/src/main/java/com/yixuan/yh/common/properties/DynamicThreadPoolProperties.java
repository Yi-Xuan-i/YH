package com.yixuan.yh.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "dynamic")
public class DynamicThreadPoolProperties {
    private boolean enabled = false;
    private Map<String, PoolParams> threadpools = new HashMap<>();

    @Data
    public static class PoolParams {
        private int corePoolSize = 1;
        private int maximumPoolSize = 2;
        private int keepAliveTime = 60;
        private int queueCapacity = 10;
    }
}
