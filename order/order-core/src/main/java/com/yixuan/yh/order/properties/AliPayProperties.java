package com.yixuan.yh.order.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "alipay")
public class AliPayProperties {
    public String url;
    public String appId;
    public String appPrivateKey;
    public String alipayPublicKey;
    public String notifyUrl;
}