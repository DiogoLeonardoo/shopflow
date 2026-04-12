package com.shopflow.order.dto;

import com.shopflow.order.domain.OrderItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    private String id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        return response;
    }
}
