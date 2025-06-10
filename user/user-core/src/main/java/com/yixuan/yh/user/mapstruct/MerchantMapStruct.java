package com.yixuan.yh.user.mapstruct;

import com.yixuan.yh.user.pojo.entity.Merchant;
import com.yixuan.yh.user.pojo.response.MerchantBasicDataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MerchantMapStruct {
    MerchantMapStruct INSTANCE = Mappers.getMapper(MerchantMapStruct.class);

    MerchantBasicDataResponse merchantToMerchantBasicDataResponse(Merchant merchant);
}
