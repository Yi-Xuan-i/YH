package com.yixuan.yh.user.controller;

import com.yixuan.yh.user.pojo.response.MarketplaceHomeDataResponse;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.user.service.HomeIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class HomeIndexController {

    @Autowired
    private HomeIndexService homeIndexService;

    @GetMapping("/marketplace/home/data")
    public Result<MarketplaceHomeDataResponse> getMarketplaceHomeData() {
        return Result.success(homeIndexService.getMarketplaceHomeData(UserContext.getUser()));
    }

}
