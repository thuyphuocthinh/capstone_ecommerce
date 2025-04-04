package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.PaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentStatus;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RefundResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.service.PaymentThirdPartyService;
import com.tpt.capstone_ecommerce.ecommerce.utils.VNPayUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Qualifier(value = "vnpay")
public class VNPayPaymentThirdPartyService implements PaymentThirdPartyService {
    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.secretKey}")
    private String secretKey;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @Value("${vnpay.apiUrl}")
    private String apiUrl;

    @Value("${vnpay.ipnUrl}")
    private String ipnUrl;

    @Override
    public PaymentResponse createPayment(PaymentRequest request, Order order) throws Exception {
        BigDecimal amount = request.getAmount();
        String ipAddress = request.getIpAddress();
        String queryUrl = VNPayUtils.generateVnpayUrl(amount.longValue(), order.getId(), ipAddress, tmnCode, returnUrl, secretKey, payUrl, ipnUrl);
        return new PaymentResponse("", queryUrl, PAYMENT_STATUS.PENDING.name());
    }

    @Override
    public PaymentStatus checkPaymentStatus(String transactionId) {
        return null;
    }
}
