package com.yixuan.yh.product.mapper.multi;

import com.yixuan.yh.product.response.ProductDetailResponse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMultiMapper {
    ProductDetailResponse selectProductDetail(Long productId);
}
