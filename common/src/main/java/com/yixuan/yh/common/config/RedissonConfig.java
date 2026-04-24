package com.yixuan.yh.common.config;

import com.yixuan.yh.common.properties.RedissonProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "redisson",
        name = "address"
)
public class RedissonConfig {

    private final RedissonProperties redissonProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        config.useSingleServer()
                .setAddress(redissonProperties.getAddress())
                .setPassword(redissonProperties.getPassword())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize());

        return Redisson.create(config);
    }
}