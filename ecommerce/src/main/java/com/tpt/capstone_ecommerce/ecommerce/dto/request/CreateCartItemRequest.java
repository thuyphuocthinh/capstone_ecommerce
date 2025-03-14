package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCartItemRequest {
    @NotBlank(message = "Cart item SKU id cannot be blank")
    @Size(min = 36, max = 36, message = "Cart item SKU id length is 36")
    private String skuId;

    @Min(value = 1, message = "SKU quantity must be greater than or equal to one")
    private int quantity;
}
