package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import com.tpt.capstone_ecommerce.ecommerce.aop.annotation.ValidEnum;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_METHOD;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceOrderRequest {
    @NotBlank(message = "Place order address id cannot be blank")
    @Size(min = 36, max = 36, message = "Place order address id length is 36")
    private String addressId;

    @ValidEnum(enumClass = PAYMENT_METHOD.class, message = "Invalid order status")
    private String paymentMethod;

    @NotEmpty(message = "Place order item ids cannot be empty")
    List<String> orderItemIds;

    OrderDiscount globalDiscounts;

    List<OrderDiscount> shopDiscounts;
}
