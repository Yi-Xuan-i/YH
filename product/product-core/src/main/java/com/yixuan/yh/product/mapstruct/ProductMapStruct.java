package com.yixuan.yh.product.mapstruct;

import com.yixuan.yh.product.pojo.model.entity.Product;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapStruct {
    ProductMapStruct INSTANCE = Mappers.getMapper(ProductMapStruct.class);

    ProductSummaryResponse toProductSummaryResponse(Product product);
}
