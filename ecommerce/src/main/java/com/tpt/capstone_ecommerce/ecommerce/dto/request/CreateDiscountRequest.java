package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDiscountRequest {
    @NotBlank(message = "Discount name cannot be blank")
    @Size(min = 2, message = "Discount name min length is 2")
    private String name;

    @NotBlank(message = "Discount description cannot be blank")
    @Size(min = 2, message = "Discount description min length is 2")
    private String description;

    @NotBlank(message = "Discount code cannot be blank")
    @Size(min = 10, message = "Discount code min length is 10")
    private String code;

    @Min(value = 0, message = "Discount value must be greater than or equal to zero")
    private double value;

    @Min(value = 0, message = "Discount min order value must be greater than or equal to zero")
    private double minOrderValue;

    @NotNull(message = "Discount start date cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "Discount end date cannot be null")
    private LocalDateTime endDate;

    @NotBlank(message = "Discount type cannot be blank")
    private String type;
}
