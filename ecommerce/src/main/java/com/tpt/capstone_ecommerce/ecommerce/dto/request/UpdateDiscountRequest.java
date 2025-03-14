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
public class UpdateDiscountRequest {
    private String name;
    private String description;
    private String code;
    private Double value;
    private Double minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String type;
}
