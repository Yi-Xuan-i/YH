package com.yixuan.yh.order.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCartOrderRequest {
    private List<Long> cartItemIdList;
    String deliveryAddress;
}
