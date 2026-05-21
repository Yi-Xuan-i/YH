package com.yixuan.yh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.order.pojo.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("select order_status from `order` where order_id = #{orderId}")
    Order.OrderStatus selectStatusByOrderId(Long orderId);

    @Select("select payment_amount from `order` where order_id = #{orderId}")
    BigDecimal selectPaymentAmountByOrderId(Long orderId);

    @Select("select order_id, user_id, merchant_id, payment_amount, order_status, delivery_address, created_at " +
            "from `order` where user_id = #{userId} and order_status = 'UNPAID' order by created_at desc")
    List<Order> selectPendingPaymentByUserId(Long userId);

    @Update("update `order` set order_status = #{orderStatus} where order_id = #{orderId}")
    void updateStatusByOrderId(String orderId, Order.OrderStatus orderStatus);

    @Update("update `order` set order_status = 'CANCELLED' where order_id = #{orderId} and order_status = 'UNPAID'")
    Boolean updateStatusToCancelIfUnPaid(Long orderId);
}
