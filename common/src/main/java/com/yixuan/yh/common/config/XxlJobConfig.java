package com.yixuan.yh.common.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Data
@ConfigurationProperties(prefix = "xxl.job")
@ConditionalOnProperty(
        prefix = "xxl.job",
        name = "admin.addresses"
)
public class XxlJobConfig {

    private String accessToken;
    private final Admin admin = new Admin();
    private final Executor executor = new Executor();

    @Data
    public static class Admin {
        private String addresses;
    }

    @Data
    public static class Executor {
        private String appname;
        private int port;
    }

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(admin.addresses);
        xxlJobSpringExecutor.setAppname(executor.appname);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setPort(executor.port);
        return xxlJobSpringExecutor;
    }
}