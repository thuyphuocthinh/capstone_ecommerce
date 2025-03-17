package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private double finalPrice;
    private String status;
    private int totalQuantity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;

    public UserOrderResponse(Order order) {
        this.id = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus().toString();
        this.totalQuantity = order.getTotalQuantity();
        this.orderDate = order.getCreatedAt();
        this.finalPrice = order.getFinalTotalPrice();
    }
}
