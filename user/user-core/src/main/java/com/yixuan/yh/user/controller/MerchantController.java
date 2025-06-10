package com.yixuan.yh.user.controller;

import com.yixuan.yh.user.pojo.request.PostMerchantRequest;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.user.pojo.response.MerchantBasicDataResponse;
import com.yixuan.yh.user.service.MerchantService;
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
