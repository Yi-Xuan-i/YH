package com.yixuan.study.microservices.user.service.impl;

import com.yixuan.mt.client.MTClient;
import com.yixuan.study.microservices.user.entity.Merchant;
import com.yixuan.study.microservices.user.mapper.MerchantMapper;
import com.yixuan.study.microservices.user.mapstruct.MerchantMapStruct;
import com.yixuan.study.microservices.user.request.PostMerchantRequest;
import com.yixuan.study.microservices.user.response.MerchantBasicDataResponse;
import com.yixuan.study.microservices.user.service.MerchantService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MTClient mtClient;

    @Override
    public void postMerchant(Long userId, PostMerchantRequest postMerchantRequest) throws IOException {

        // 判断是否已经有小店
        if (merchantMapper.selectIsMerchant(userId)) {
            throw new BadRequestException("每个账号只能有一个小店！");
        }

        Merchant merchant = new Merchant();
        merchant.setMerchantId(userId);
        merchant.setName(postMerchantRequest.getName());
        merchant.setContactPhone(postMerchantRequest.getContactPhone());
        merchant.setAvatarUrl(mtClient.upload(postMerchantRequest.getAvatar()));

        merchantMapper.insert(merchant);
    }

    @Override
    public MerchantBasicDataResponse getBasicMerchant(Long userId) {
        return MerchantMapStruct.INSTANCE.merchantToMerchantBasicDataResponse(merchantMapper.selectBasic(userId));
    }
}
