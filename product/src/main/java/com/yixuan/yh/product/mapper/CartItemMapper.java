package com.yixuan.yh.product.mapper;

import com.yixuan.yh.product.model.entity.CartItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper {
    @Insert("insert into cart_item (cart_item_id, user_id, product_id, sku_id, merchant_id, quantity, price, selected_sku) values(#{cartItemId}, #{userId}, #{productId}, #{skuId}, #{merchantId}, #{quantity}, #{price}, #{selectedSku})")
    void insert(CartItem cartItem);
}
