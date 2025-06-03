package com.yixuan.yh.product.service.impl;

import com.yixuan.yh.commom.utils.SnowflakeUtils;
import com.yixuan.yh.product.mapper.CartItemMapper;
import com.yixuan.yh.product.mapper.ProductMapper;
import com.yixuan.yh.product.mapper.ProductSkuMapper;
import com.yixuan.yh.product.mapstruct.CartMapStruct;
import com.yixuan.yh.product.model.entity.CartItem;
import com.yixuan.yh.product.request.PostCartItemRequest;
import com.yixuan.yh.product.service.CartService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

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
}
