package com.yixuan.yh.common.config;

import com.yixuan.yh.common.properties.DynamicThreadPoolProperties;
import com.yixuan.yh.common.threapool.DynamicThreadPoolPostProcessor;
import com.yixuan.yh.common.threapool.DynamicThreadPoolRefreshListener;
import com.yixuan.yh.common.threapool.DynamicThreadPoolRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "dynamic", name = "enabled", havingValue = "true")
public class DynamicThreadPoolConfig {

    @Bean
    public DynamicThreadPoolPostProcessor dynamicThreadPoolInitializer() {
        return new DynamicThreadPoolPostProcessor();
    }

    @Bean
    public DynamicThreadPoolRefreshListener dynamicThreadPoolRefreshListener(DynamicThreadPoolProperties properties, DynamicThreadPoolRegistry registry) {
        return new DynamicThreadPoolRefreshListener(registry, properties);
    }

    @Bean
    public DynamicThreadPoolRegistry dynamicThreadPoolRegistry() {
        return new DynamicThreadPoolRegistry();
    }
}
