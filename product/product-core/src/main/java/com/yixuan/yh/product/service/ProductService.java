package com.yixuan.yh.product.service;

import com.yixuan.yh.product.pojo.response.ProductDetailResponse;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;

import java.util.List;

public interface ProductService {
    List<ProductSummaryResponse> getProducts();

    ProductDetailResponse getDetailProducts(Long productId);
}
