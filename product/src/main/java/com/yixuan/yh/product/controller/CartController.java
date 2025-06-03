package com.yixuan.yh.product.controller;

import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.commom.utils.UserContext;
import com.yixuan.yh.product.request.PostCartItemRequest;
import com.yixuan.yh.product.response.CartItemResponse;
import com.yixuan.yh.product.service.CartService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public Result<Void> postCartItem(@RequestBody PostCartItemRequest postCartItemRequest) throws BadRequestException {
        cartService.postCartItem(UserContext.getUser(), postCartItemRequest);
        return Result.success();
    }
}
