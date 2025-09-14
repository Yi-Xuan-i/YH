package com.yixuan.yh.order.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.yixuan.yh.order.properties.AliPayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {

    @Autowired
    AliPayProperties aliPayProperties;

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(aliPayProperties.url, aliPayProperties.appId, aliPayProperties.appPrivateKey,
                "json", "UTF-8", aliPayProperties.alipayPublicKey, "RSA2");
    }
}
