package com.yixuan.yh.order.consumer;

import com.alipay.api.AlipayApiException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.order.constant.RabbitMQConstant;
import com.yixuan.yh.order.mq.OrderExpirationMessage;
import com.yixuan.yh.order.service.OrderService;
import com.yixuan.yh.product.feign.ProductPrivateClient;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderConsumer {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductPrivateClient productPrivateClient;

//    @RabbitListener(queues = "order.queue", containerFactory = "batchContainerFactory")
//    public void handleOrderMessage(List<String> orderMessageList) {
//        // 批量更新语句（应对脉冲流量）
//        String sql = "INSERT INTO product_sales_counter (product_id, date, daily_sales, total_sales) " +
//                "VALUES (?, CURDATE(), ?, ?) " +
//                "ON DUPLICATE KEY UPDATE " +
//                "daily_sales = daily_sales + VALUES(daily_sales), " +
//                "total_sales = total_sales + VALUES(total_sales)";
//    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstant.ORDER_DELAY_QUEUE, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstant.ORDER_DELAY_EXCHANGE, durable = "true", delayed = "true"),
            key = RabbitMQConstant.ORDER_DELAY_QUEUE_KEY
    ))
    public void handleOrderExpiration(OrderExpirationMessage orderExpirationMessage) throws AlipayApiException {
        // 更新订单状态
        if (orderService.putToCancelIfUnpaid(orderExpirationMessage.getOrderId())) {
            System.out.println("归还库存！");
            // 归还占用库存
            Result<Void> result = productPrivateClient.putReservedStock(orderExpirationMessage.getSkuId(), Map.of("quantity", orderExpirationMessage.getQuantity()));
            if (result.isError()) {
                throw new RuntimeException();
            }
        }
    }
}
