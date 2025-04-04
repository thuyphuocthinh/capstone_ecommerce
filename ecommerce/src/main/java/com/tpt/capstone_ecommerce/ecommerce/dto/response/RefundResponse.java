package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponse {
    private String refundId;  // Mã hoàn tiền từ Stripe hoặc hệ thống của VNPay (nếu hỗ trợ)
    private String status;    // Trạng thái hoàn tiền (PENDING, SUCCESS, FAILED)
}

