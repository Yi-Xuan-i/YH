package com.yixuan.yh.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.product.pojo.model.entity.CartItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
    @Insert("insert into cart_item (cart_item_id, user_id, product_id, sku_id, quantity, price, selected_sku) values(#{cartItemId}, #{userId}, #{productId}, #{skuId}, #{quantity}, #{price}, #{selectedSku})")
    int insert(CartItem cartItem);

    @Select("select * from cart_item where user_id = #{userId}")
    List<CartItem> selectByUserId(Long userId);
}
