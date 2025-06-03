package com.yixuan.yh.product.service;

import com.yixuan.yh.product.request.PostCartItemRequest;
import org.apache.coyote.BadRequestException;

public interface CartService {
    void postCartItem(Long userId, PostCartItemRequest postCartItemRequest) throws BadRequestException;
}
