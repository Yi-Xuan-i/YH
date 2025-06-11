package com.yixuan.yh.live;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.yixuan.yh.user.feign"})
@EnableCaching
public class LiveApplication {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void init() {
        elasticsearchOperations.indexOps(IndexCoordinates.of("live")).delete();
        System.out.println("666666");
    }

    public static void main(String[] args) {
        SpringApplication.run(LiveApplication.class, args);
    }
}