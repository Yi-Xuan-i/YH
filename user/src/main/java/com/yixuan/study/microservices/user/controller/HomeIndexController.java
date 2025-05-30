package com.yixuan.study.microservices.user.controller;

import com.yixuan.study.microservices.user.response.MarketplaceHomeDataResponse;
import com.yixuan.study.microservices.user.service.HomeIndexService;
import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.commom.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeIndexController {

    @Autowired
    private HomeIndexService homeIndexService;

    @GetMapping("/marketplace/home/data")
    public Result<MarketplaceHomeDataResponse> getMarketplaceHomeData() {
        return Result.success(homeIndexService.getMarketplaceHomeData(UserContext.getUser()));
    }

}
