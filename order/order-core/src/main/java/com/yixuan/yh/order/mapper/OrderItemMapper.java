package com.yixuan.yh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.order.pojo.entity.OrderItem;
import com.yixuan.yh.order.pojo.response.MerchantDailySalesResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    @Select("select order_item_id, order_id, product_id, sku_id, sku, product_name, quantity, price from order_item where order_id = #{orderId}")
    List<OrderItem> selectByOrderId(Long orderId);

    @Select("""
            <script>
            select
                coalesce(sum(quantity), 0) as daily_sales_volume,
                coalesce(sum(price * quantity), 0) as daily_sales_amount
            from order_item
            where pay_time &gt;= #{startTime}
              and pay_time &lt; #{endTime}
              and product_id in
              <foreach collection="productIdList" item="productId" open="(" close=")" separator=",">
                  #{productId}
              </foreach>
            </script>
            """)
    @Results({
            @Result(column = "daily_sales_volume", property = "dailySalesVolume"),
            @Result(column = "daily_sales_amount", property = "dailySalesAmount")
    })
    MerchantDailySalesResponse selectMerchantDailySales(@Param("productIdList") List<Long> productIdList,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);
}
