package com.yixuan.yh.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "srs")
public record SrsProperties(String url) {}