package com.yixuan.yh.product.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.product.pojo.request.PostCartItemRequest;
import com.yixuan.yh.product.pojo.response.CartItemResponse;
import com.yixuan.yh.product.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cart")
@RestController
@RequestMapping("/me/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "商品加入购物车")
    @PostMapping
    public Result<Void> postCartItem(@RequestBody PostCartItemRequest postCartItemRequest) throws BadRequestException {
        cartService.postCartItem(UserContext.getUser(), postCartItemRequest);
        return Result.success();
    }

    @Operation(summary = "获取购物车商品")
    @GetMapping
    public Result<List<CartItemResponse>> getCartItem() {
        return Result.success(cartService.getCartItem(UserContext.getUser()));
    }
}
