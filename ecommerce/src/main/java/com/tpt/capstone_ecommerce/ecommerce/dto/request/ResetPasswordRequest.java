package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password min length is 8")
    private String password;

    @NotBlank(message = "Confirm password cannot be blank")
    @Size(min = 8, message = "Confirm password min length is 8")
    private String confirmPassword;

    @NotBlank(message = "Otp cannot be blank")
    @Size(min = 36, max = 36, message = "Otp length is 36")
    private String otp;
}
