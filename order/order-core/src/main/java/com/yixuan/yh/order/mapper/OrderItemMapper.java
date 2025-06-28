package com.yixuan.yh.order.mapper;

import com.yixuan.yh.order.pojo.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper {
    @Insert("insert into order_item (order_item_id, order_id, product_id, sku_id, sku, product_name, quantity, price) values(#{orderItemId}, #{orderId}, #{productId}, #{skuId}, #{sku}, #{productName}, #{quantity}, #{price})")
    void insert(OrderItem orderItem);
}
