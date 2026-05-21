package com.yixuan.yh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.order.pojo.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    @Select("select order_item_id, order_id, product_id, sku_id, sku, product_name, quantity, price from order_item where order_id = #{orderId}")
    List<OrderItem> selectByOrderId(Long orderId);
}
