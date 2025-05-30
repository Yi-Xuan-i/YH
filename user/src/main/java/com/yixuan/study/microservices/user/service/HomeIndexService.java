package com.yixuan.study.microservices.user.service;

import com.yixuan.study.microservices.user.response.MarketplaceHomeDataResponse;

public interface HomeIndexService {
    MarketplaceHomeDataResponse getMarketplaceHomeData(Long userId);
}
