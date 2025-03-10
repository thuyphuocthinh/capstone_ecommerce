package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class UserOrderResponse {
    private String id;
    private double totalPrice;
    private String status;
    private int totalQuantity;
    private LocalDateTime orderDate;

    public UserOrderResponse(Order order) {
        this.id = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus().toString();
        this.totalQuantity = order.getTotalQuantity();
        this.orderDate = order.getCreatedAt();
    }
}
