package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkPaymentStatusHandler(
            @RequestParam(value = "order_id", required = true) String orderId
    ) {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .message("Success")
                .data(this.paymentService.getPaymentStatus(orderId))
                .build();
        return ResponseEntity.ok(apiSuccessResponse);
    }
}
