package com.yixuan.yh.common.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new Jackson2ObjectMapperBuilder()
                .modulesToInstall(JavaTimeModule.class) // 支持Java8时间类型
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(formatter))
                .timeZone("GMT+8"); // 设置时区
    }
}