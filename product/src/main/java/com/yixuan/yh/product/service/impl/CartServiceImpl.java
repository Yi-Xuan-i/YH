package com.yixuan.yh.product.service.impl;

import com.yixuan.yh.commom.utils.SnowflakeUtils;
import com.yixuan.yh.product.mapper.CartItemMapper;
import com.yixuan.yh.product.mapper.ProductMapper;
import com.yixuan.yh.product.mapper.ProductSkuMapper;
import com.yixuan.yh.product.mapper.multi.ProductMultiMapper;
import com.yixuan.yh.product.mapstruct.CartMapStruct;
import com.yixuan.yh.product.model.entity.CartItem;
import com.yixuan.yh.product.model.entity.ProductSku;
import com.yixuan.yh.product.model.multi.ProductPartOfCartItem;
import com.yixuan.yh.product.request.PostCartItemRequest;
import com.yixuan.yh.product.response.CartItemResponse;
import com.yixuan.yh.product.service.CartService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductMultiMapper productMultiMapper;

    @Override
    public void postCartItem(Long userId, PostCartItemRequest postCartItemRequest) throws BadRequestException {
        // 检查商品是否有该 sku
        if (!postCartItemRequest.getProductId().equals(productSkuMapper.selectProductIdBySkuId(postCartItemRequest.getSkuId()))) {
            throw new BadRequestException("非法操作！");
        }

        CartItem cartItem = CartMapStruct.INSTANCE.postCartItemRequestToCartItem(postCartItemRequest);
        cartItem.setCartItemId(snowflakeUtils.nextId());
        cartItem.setUserId(userId);

        cartItemMapper.insert(cartItem);
    }

    @Override
    public List<CartItemResponse> getCartItem(Long userId) {
        List<CartItem> cartItemList = cartItemMapper.selectByUserId(userId);

        // 数据转换
        List<CartItemResponse> cartItemResponseList = new ArrayList<>(cartItemList.size());
        for (CartItem cartItem : cartItemList) {
            cartItemResponseList.add(CartMapStruct.INSTANCE.cartItemToCartItemResponse(cartItem));
        }

        // 提取所有商品ID和SKU ID
        List<Long> productIdList = cartItemList.stream().map(CartItem::getProductId).toList();
        List<Long> skuIdList = cartItemList.stream().map(CartItem::getSkuId).toList();

        // 批量查询商品和SKU信息
        List<ProductPartOfCartItem> productList = productMultiMapper.selectPartOfCartItem(productIdList);
        List<ProductSku> productSkuList = productSkuMapper.selectPartOfCartItem(skuIdList);

        // 构建ID映射关系
        Map<Long, ProductPartOfCartItem> productMap = productList.stream()
                .collect(Collectors.toMap(ProductPartOfCartItem::getProductId, Function.identity()));

        Map<Long, ProductSku> skuMap = productSkuList.stream()
                .collect(Collectors.toMap(ProductSku::getSkuId, Function.identity()));

        // 按购物车项顺序关联数据
        for (int i = 0; i < cartItemList.size(); i++) {
            CartItem item = cartItemList.get(i);
            CartItemResponse response = cartItemResponseList.get(i);

            // 关联商品信息
            ProductPartOfCartItem product = productMap.get(item.getProductId());
            if (product != null) {
                response.setMerchantId(product.getMerchantId());
                response.setMerchantName(product.getMerchantName());
                response.setProductTitle(product.getTitle());
                response.setCoverUrl(product.getCoverUrl());
            }

            // 关联SKU信息
            ProductSku sku = skuMap.get(item.getSkuId());
            if (sku != null) {
               response.setCurrentPrice(sku.getPrice());
               response.setStock(sku.getStock());
            }
        }

        return cartItemResponseList;
    }
}
