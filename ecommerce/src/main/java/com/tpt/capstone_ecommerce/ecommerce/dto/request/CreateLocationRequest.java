package com.tpt.capstone_ecommerce.ecommerce.dto.request;


import com.tpt.capstone_ecommerce.ecommerce.enums.LOCATION_TYPE;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLocationRequest {
    @NotBlank(message = "Location name cannot be blank")
    @Size(min = 8, message = "Location name min length is 2")
    private String name;

    private String parentId;

    @NotNull(message = "Location type cannot be null")
    private LOCATION_TYPE locationType;
}
