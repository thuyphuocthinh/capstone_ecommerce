package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.PaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RetryPaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @PostMapping("/retry")
    public ResponseEntity<?> retryPaymentHandler(
            @Valid @RequestBody RetryPaymentRequest retryPaymentRequest,
            @RequestHeader(value = "X-Forwarded-For", required = false) Optional<String> forwardedForOpt,
            HttpServletRequest request
    ) throws Exception {
        String ip = forwardedForOpt.filter(f -> !f.isEmpty()).orElse(request.getRemoteAddr());
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .message("Success")
                .data(this.paymentService.retryOnlinePaymentHandler(retryPaymentRequest, ip))
                .build();
        return ResponseEntity.ok(apiSuccessResponse);
    }
}
