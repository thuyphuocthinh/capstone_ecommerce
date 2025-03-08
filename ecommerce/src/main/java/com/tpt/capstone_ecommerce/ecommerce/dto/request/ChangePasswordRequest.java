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
public class ChangePasswordRequest {
    @NotBlank(message = "Old password cannot be blank")
    @Size(min = 8, message = "Password min length is 8")
    private String oldPassword;

    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, message = "Password min length is 8")
    private String newPassword;

    @NotBlank(message = "Confirm password cannot be blank")
    @Size(min = 8, message = "Password min length is 8")
    private String confirmPassword;
}
