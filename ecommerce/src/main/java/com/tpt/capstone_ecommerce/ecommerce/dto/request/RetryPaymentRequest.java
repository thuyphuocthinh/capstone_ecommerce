package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import com.tpt.capstone_ecommerce.ecommerce.aop.annotation.ValidEnum;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_THIRD_PARTIES;
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
public class RetryPaymentRequest {
    @NotBlank(message = "Retry payment request order id is required")
    @Size(min = 36, max = 36, message = "Length of order id is 36")
    private String orderId;

    @NotBlank(message = "Retry payment request payment id is required")
    @Size(min = 36, max = 36, message = "Length of payment id is 36")
    private String paymentId;

    @ValidEnum(enumClass = PAYMENT_THIRD_PARTIES.class)
    private PAYMENT_THIRD_PARTIES paymentThirdParty;
}
