package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.PaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RetryPaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RetryPaymentResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest paymentRequest, Order order, String paymentThirdParty) throws Exception;
    void updatePaymentStatusAndTransactionIdByOrderId(String orderId, String status, String transactionId) throws NotFoundException;
    String getPaymentStatus(String orderId) throws NotFoundException;
    void createPaymentCash(Order order);
    void updatePaymentCash(Order order) throws NotFoundException;
    RetryPaymentResponse retryOnlinePaymentHandler(RetryPaymentRequest request, String ipAddress) throws Exception;
}
