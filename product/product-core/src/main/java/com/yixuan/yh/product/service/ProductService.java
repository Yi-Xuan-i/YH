package com.yixuan.yh.product.service;

import com.yixuan.yh.common.mybatis.BaseIService;
import com.yixuan.yh.product.pojo.model.entity.Product;
import com.yixuan.yh.product.pojo.response.PartOfCartOrderResponse;
import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import com.yixuan.yh.product.pojo.response.ProductDetailResponse;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Map;

public interface ProductService extends BaseIService<Product> {
    List<ProductSummaryResponse> getProducts();

    List<ProductSummaryResponse> searchProducts(String keyword);

    ProductDetailResponse getDetailProducts(Long productId);

    PartOfOrderResponse getPartOfOrder(Long orderId, Long productId, Long skuId, Integer quantity) throws BadRequestException, InterruptedException;

    Map<Long, PartOfCartOrderResponse> getPartOfCartOrder(Long orderId, List<Long> cartItemIdList);

    void putReservedStock(Long skuId, Integer quantity);

    void increaseSalesVolume(Map<Long, Integer> productQuantityMap);
}
