package com.yixuan.yh.product.mapstruct;

import com.yixuan.yh.product.model.entity.Product;
import com.yixuan.yh.product.model.entity.ProductSku;
import com.yixuan.yh.product.request.PutProductBasicInfoRequest;
import com.yixuan.yh.product.request.PutSkuRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MerchantMapStruct {
    MerchantMapStruct INSTANCE = Mappers.getMapper(MerchantMapStruct.class);

    Product putProductBasicInfoRequestToProduct(PutProductBasicInfoRequest putProductBasicInfoRequest);
    ProductSku putSkuRequestToProductSku(PutSkuRequest.PutSku putSku);
}
