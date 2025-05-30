package com.yixuan.study.microservices.user.service.impl;

import com.yixuan.study.microservices.user.mapper.MerchantMapper;
import com.yixuan.study.microservices.user.response.MarketplaceHomeDataResponse;
import com.yixuan.study.microservices.user.service.HomeIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeIndexServiceImpl implements HomeIndexService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public MarketplaceHomeDataResponse getMarketplaceHomeData(Long userId) {
        MarketplaceHomeDataResponse response = new MarketplaceHomeDataResponse();
        response.setIsMerchant(merchantMapper.selectIsMerchant(userId));

        return response;
    }
}
