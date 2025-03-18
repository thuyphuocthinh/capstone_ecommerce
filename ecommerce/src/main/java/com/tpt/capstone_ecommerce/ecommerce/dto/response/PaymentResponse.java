package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PaymentResponse {
    private String transactionId;  // ID giao dịch từ cổng thanh toán (Stripe: PaymentIntent ID, VNPay: không có)
    private String status;         // Trạng thái thanh toán (PENDING, SUCCESS, FAILED)
    private String redirectUrl;    // Link redirect nếu dùng cổng như VNPay
}
