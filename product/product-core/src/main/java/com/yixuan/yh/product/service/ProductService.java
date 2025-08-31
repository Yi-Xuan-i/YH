package com.yixuan.yh.product.service;

import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import com.yixuan.yh.product.pojo.response.ProductDetailResponse;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface ProductService {
    List<ProductSummaryResponse> getProducts();

    ProductDetailResponse getDetailProducts(Long productId);

    PartOfOrderResponse getPartOfOrder(Long orderId, Long productId, Long skuId, Integer quantity) throws BadRequestException, InterruptedException;

    void putReservedStock(Long skuId, Integer quantity);
}
