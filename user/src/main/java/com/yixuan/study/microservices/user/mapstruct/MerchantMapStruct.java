package com.yixuan.study.microservices.user.mapstruct;

import com.yixuan.study.microservices.user.entity.Merchant;
import com.yixuan.study.microservices.user.response.MerchantBasicDataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MerchantMapStruct {
    MerchantMapStruct INSTANCE = Mappers.getMapper(MerchantMapStruct.class);

    MerchantBasicDataResponse merchantToMerchantBasicDataResponse(Merchant merchant);
}
