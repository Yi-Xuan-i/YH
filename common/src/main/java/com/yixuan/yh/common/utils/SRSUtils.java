package com.yixuan.yh.common.utils;

import com.yixuan.yh.common.properties.SrsProperties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Lazy
@RequiredArgsConstructor
public class SRSUtils {
    private final SrsProperties srsProperties;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
    }

    public Set<Long> getStreams() {
        SrsStreamResponse response = restTemplate.getForObject(srsProperties.url() + "/api/v1/streams", SrsStreamResponse.class);

        return response.getStreams().stream()
                .map(SrsStreamResponse.SrsStream::getName)
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    @Data
    public static class SrsStreamResponse {
        private int code;
        private List<SrsStream> streams;

        @Data
        public static class SrsStream {
            private String id;
            private String name;
            private String vhost;
            private SrsPublish publish; // 包含推流信息
        }

        @Data
        public static class SrsPublish {
            private boolean active;
        }
    }
}
