package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSkuRequest {
    @NotBlank(message = "SPU name cannot be blank")
    @Size(min = 2, message = "SPU name min length is 2")
    private String name;

    @NotBlank(message = "SPU size cannot be blank")
    @Size(min = 2, message = "SPU size min length is 2")
    private String size;

    @NotBlank(message = "SPU color cannot be blank")
    @Size(min = 2, message = "SPU color min length is 2")
    private String color;

    @NotBlank(message = "SKU price cannot be blank")
    @Min(value = 0, message = "SKU price must be greater than or equal to zero")
    private double price;

    @NotBlank(message = "SKU discount cannot be blank")
    @Min(value = 0, message = "SKU discount must be greater than or equal to zero")
    private double discount;

    @NotBlank(message = "SKU quantity cannot be blank")
    @Min(value = 0, message = "SKU quantity must be greater than or equal to zero")
    private int quantity;

    @NotNull(message = "SPU image cannot be null")
    private MultipartFile file;
}
