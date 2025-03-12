package com.tpt.capstone_ecommerce.ecommerce.dto.request;


import com.tpt.capstone_ecommerce.ecommerce.enums.USER_ROLE;
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
public class CreateShopRequest {
    @NotBlank(message = "Shop name cannot be blank")
    @Size(min = 2, message = "Shop name min length is 2")
    private String name;

    @NotBlank(message = "Shop description cannot be blank")
    @Size(min = 8, message = "Shop description min length is 8")
    private String description;

    @NotNull(message = "Shop image cannot be null")
    private MultipartFile file;

    @NotBlank(message = "Shop phone cannot be blank")
    @Size(min = 10, max = 15, message = "Address phone length is from 10 - 15")
    private String phone;

    @NotBlank(message = "Shop location id cannot be blank")
    @Size(min = 36, max = 36, message = "Shop location id length is 36")
    private String locationId;

    @NotBlank(message = "Shop specific address cannot be blank")
    @Size(min = 2, message = "Shop specific address min length is 2")
    private String specificAddress;

    @NotBlank(message = "Shop owner id cannot be blank")
    @Size(min = 36, max = 36, message = "Shop owner id length is 36")
    private String ownerId;
}
