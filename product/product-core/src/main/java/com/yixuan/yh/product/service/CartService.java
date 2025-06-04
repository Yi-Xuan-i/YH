package com.yixuan.yh.product.service;

import com.yixuan.yh.product.request.PostCartItemRequest;
import com.yixuan.yh.product.response.CartItemResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface CartService {
    void postCartItem(Long userId, PostCartItemRequest postCartItemRequest) throws BadRequestException;

    List<CartItemResponse> getCartItem(Long userId);
}
