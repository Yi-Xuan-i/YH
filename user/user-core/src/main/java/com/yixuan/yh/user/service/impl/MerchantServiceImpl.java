package com.yixuan.yh.user.service.impl;

import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.user.mapper.MerchantMapper;
import com.yixuan.yh.user.mapstruct.MerchantMapStruct;
import com.yixuan.yh.user.pojo.entity.Merchant;
import com.yixuan.yh.user.pojo.request.PostMerchantRequest;
import com.yixuan.yh.user.pojo.request.PutMerchantRequest;
import com.yixuan.yh.user.pojo.response.MerchantBasicDataResponse;
import com.yixuan.yh.user.service.MerchantService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private AWSUtils awsUtils;

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
        merchant.setAvatarUrl(awsUtils.putObject(postMerchantRequest.getAvatar()));
        merchant.setCertificationStatus(Merchant.CertificationStatus.CERTIFIED); // 先默认认证通过，后续可以加一个人工审核的流程

        merchantMapper.insert(merchant);
    }

    @Override
    public MerchantBasicDataResponse getBasicMerchant(Long userId) {
        Merchant merchant = merchantMapper.selectBasic(userId);
        if (merchant == null) {
            return null;
        }

        MerchantBasicDataResponse response = MerchantMapStruct.INSTANCE.toMerchantBasicDataResponse(merchant);
        response.setAvatarUrl(awsUtils.generateAccessUrl(response.getAvatarUrl()));
        return response;
    }

    @Override
    public void putMerchant(Long userId, PutMerchantRequest putMerchantRequest) throws IOException {
        checkMerchantExist(userId);
        merchantMapper.updateBasic(userId, putMerchantRequest);
    }

    @Override
    public String postAvatar(Long userId, MultipartFile avatar) throws IOException {
        checkMerchantExist(userId);
        String newAvatarKey = awsUtils.putObject(avatar);
        merchantMapper.updateAvatarUrl(userId, newAvatarKey);
        return awsUtils.generateAccessUrl(newAvatarKey);
    }

    private void checkMerchantExist(Long userId) throws BadRequestException {
        if (!merchantMapper.selectIsMerchant(userId)) {
            throw new BadRequestException("店铺不存在！");
        }
    }
}
