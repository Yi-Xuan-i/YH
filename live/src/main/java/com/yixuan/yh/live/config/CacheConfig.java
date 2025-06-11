package com.yixuan.yh.live.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100) // 初始容量
                .maximumSize(1000)    // 最大条目数
                .expireAfterWrite(5, TimeUnit.MINUTES)); // 写入30分钟后过期
//                .recordStats());      // 开启统计
        return cacheManager;
    }
}
