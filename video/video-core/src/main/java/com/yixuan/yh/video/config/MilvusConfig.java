package com.yixuan.yh.video.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "milvus")
public class MilvusConfig {

    private String uri;

    @Bean
    public MilvusClientV2 milvusClient() {
        try {
            ConnectConfig config = ConnectConfig.builder()
                    .uri(uri)
                    .build();
            return new MilvusClientV2(config);
        } catch (Exception e) {
            return new MilvusClientV2(null);
        }
    }
}
