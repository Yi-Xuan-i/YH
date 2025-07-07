package com.yixuan.yh.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClient() {
        // 配置类
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://110.41.182.125:6379")
                .setPassword("qq2434462503")
                .setConnectionPoolSize(6)
                .setConnectionMinimumIdleSize(2);

        return Redisson.create(config);
    }
}
