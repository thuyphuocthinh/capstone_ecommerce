package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentStatus;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.service.PaymentService;
import com.tpt.capstone_ecommerce.ecommerce.service.factory.PaymentServiceFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/vn-pay/")
public class VNPayPaymentController {
    private final PaymentService paymentService;

    public VNPayPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/callback")
    public ResponseEntity<PaymentStatus> handleVNPayCallback(@RequestParam Map<String, String> params) {
        String orderId = params.get("vnp_TxnRef");
        String transactionId = params.get("vnp_TransactionNo");
        String status = "00".equals(params.get("vnp_ResponseCode")) ? PAYMENT_STATUS.SUCCESS.name() : PAYMENT_STATUS.FAILED.name();
        this.paymentService.updatePaymentStatusAndTransactionIdByOrderId(orderId, transactionId, status);
        return ResponseEntity.ok(new PaymentStatus(orderId, transactionId, status));
    }
}
