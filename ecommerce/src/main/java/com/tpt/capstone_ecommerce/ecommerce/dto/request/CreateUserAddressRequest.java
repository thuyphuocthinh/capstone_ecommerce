package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserAddressRequest {
    @NotBlank(message = "Address fullname cannot be blank")
    @Size(min = 8, message = "Address fullname min length is 8")
    private String fullName;

    @NotBlank(message = "Address phone cannot be blank")
    @Size(min = 10, max = 15, message = "Address phone length is from 10 - 15")
    private String phone;

    @NotBlank(message = "Specific address be blank")
    @Size(min = 1, message = "Specific address min length is 1")
    private String specificAddress;

    @NotBlank(message = "Location id be blank")
    @Size(min = 36, max = 36, message = "Location id length is 36")
    private String locationId;
}
