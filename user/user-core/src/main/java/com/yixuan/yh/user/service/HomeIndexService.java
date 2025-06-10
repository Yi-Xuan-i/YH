package com.yixuan.yh.user.service;

import com.yixuan.yh.user.pojo.response.MarketplaceHomeDataResponse;

public interface HomeIndexService {
    MarketplaceHomeDataResponse getMarketplaceHomeData(Long userId);
}
