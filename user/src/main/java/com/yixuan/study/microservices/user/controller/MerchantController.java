package com.yixuan.study.microservices.user.controller;

import com.yixuan.study.microservices.user.request.PostMerchantRequest;
import com.yixuan.study.microservices.user.response.MerchantBasicDataResponse;
import com.yixuan.study.microservices.user.service.MerchantService;
import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.commom.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/me/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @PostMapping
    public Result<Void> postMerchant(@ModelAttribute PostMerchantRequest postMerchantRequest) throws IOException {
        merchantService.postMerchant(UserContext.getUser(), postMerchantRequest);
        return Result.success();
    }

    @GetMapping("/basic")
    public Result<MerchantBasicDataResponse> getBasicMerchant() {
        return Result.success(merchantService.getBasicMerchant(UserContext.getUser()));
    }
}
