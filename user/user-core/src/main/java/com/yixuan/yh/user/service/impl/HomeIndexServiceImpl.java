package com.yixuan.yh.user.service.impl;

import com.yixuan.yh.user.mapper.MerchantMapper;
import com.yixuan.yh.user.pojo.response.MarketplaceHomeDataResponse;
import com.yixuan.yh.user.service.HomeIndexService;
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
