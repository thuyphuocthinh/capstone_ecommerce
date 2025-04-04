package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.PaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentStatus;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RefundResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Order;

public interface PaymentThirdPartyService {
    PaymentResponse createPayment(PaymentRequest request, Order order) throws Exception;
    PaymentStatus checkPaymentStatus(String transactionId);
}
