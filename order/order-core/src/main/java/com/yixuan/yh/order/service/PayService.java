package com.yixuan.yh.order.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface PayService {
    String payNotify(Map<String, String> params) throws AlipayApiException;
}
