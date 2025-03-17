package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatus {
    private String transactionId;  // ID giao dịch từ cổng thanh toán
    private String status;         // Trạng thái giao dịch (PENDING, SUCCESS, FAILED, REFUNDED)
    private String orderId;
}
