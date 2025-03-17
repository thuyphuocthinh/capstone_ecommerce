package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private BigDecimal amount;     // Số tiền cần thanh toán
    private String currency;       // Đơn vị tiền tệ (VND, USD)
    private String ipAddress;
}