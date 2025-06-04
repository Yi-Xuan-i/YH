package com.yixuan.yh.product.mapstruct;

import com.yixuan.yh.product.pojo.model.entity.CartItem;
import com.yixuan.yh.product.pojo.request.PostCartItemRequest;
import com.yixuan.yh.product.pojo.response.CartItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CartMapStruct {
    CartMapStruct INSTANCE = Mappers.getMapper(CartMapStruct.class);

    CartItem postCartItemRequestToCartItem(PostCartItemRequest postCartItemRequest);
    CartItemResponse cartItemToCartItemResponse(CartItem cartItem);
}
