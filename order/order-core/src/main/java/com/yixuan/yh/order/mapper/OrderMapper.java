package com.yixuan.yh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.order.pojo.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("select order_status from `order` where order_id = #{orderId}")
    Order.OrderStatus selectStatusByOrderId(Long orderId);

    @Select("select payment_amount from `order` where order_id = #{orderId}")
    BigDecimal selectPaymentAmountByOrderId(Long orderId);

    @Update("update `order` set order_status = #{orderStatus} where order_id = #{orderId}")
    void updateStatusByOrderId(String orderId, Order.OrderStatus orderStatus);

    @Update("update `order` set order_status = 'CANCELLED' where order_id = #{orderId} and order_status = 'UNPAID'")
    Boolean updateStatusToCancelIfUnPaid(Long orderId);
}
