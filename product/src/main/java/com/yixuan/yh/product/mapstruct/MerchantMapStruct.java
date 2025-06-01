package com.yixuan.yh.product.mapstruct;

import com.yixuan.yh.product.model.entity.Product;
import com.yixuan.yh.product.request.PutProductBasicInfoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MerchantMapStruct {
    MerchantMapStruct INSTANCE = Mappers.getMapper(MerchantMapStruct.class);

    Product putProductBasicInfoRequestToProduct(PutProductBasicInfoRequest putProductBasicInfoRequest);
}
