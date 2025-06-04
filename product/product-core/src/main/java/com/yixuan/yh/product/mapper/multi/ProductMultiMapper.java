package com.yixuan.yh.product.mapper.multi;

import com.yixuan.yh.product.pojo.model.multi.ProductPartOfCartItem;
import com.yixuan.yh.product.pojo.response.ProductDetailResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMultiMapper {
    ProductDetailResponse selectProductDetail(Long productId);

    List<ProductPartOfCartItem> selectPartOfCartItem(List<Long> productIdList);
}
