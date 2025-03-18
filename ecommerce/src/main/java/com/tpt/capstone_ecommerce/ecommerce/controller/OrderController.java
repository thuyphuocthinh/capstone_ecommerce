package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.auth.jwt.JwtProvider;
import com.tpt.capstone_ecommerce.ecommerce.constant.HttpRequestConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CheckoutOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PlaceOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    private final JwtProvider jwtProvider;

    public OrderController(OrderService orderService, JwtProvider jwtProvider) {
        this.orderService = orderService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/{id}/retry-payment")
    public ResponseEntity<?> orderRetryPaymentHandler(@PathVariable String id) {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .message("Success")
                .data(this.orderService.getOrderRetryPayment(id))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PostMapping("/check-out")
    public ResponseEntity<?> checkOutHandler(@Valid @RequestBody CheckoutOrderRequest checkoutOrderRequest) {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .message("Success")
                .data(this.orderService.checkoutOrder(checkoutOrderRequest))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrderHandler(
            @Valid @RequestBody PlaceOrderRequest placeOrderRequest,
            @RequestHeader(HttpRequestConstant.REQUEST_AUTHORIZATION) String bearerToken,
            @RequestHeader(value = "X-Forwarded-For", required = false) Optional<String> forwardedForOpt,
            HttpServletRequest request
    ) throws Exception {
        String accessToken = bearerToken.substring(7);
        String email = this.jwtProvider.getEmailFromToken(accessToken);
        String ip = forwardedForOpt.filter(f -> !f.isEmpty()).orElse(request.getRemoteAddr());
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .message("Success")
                .data(this.orderService.placeOrder(email, placeOrderRequest, ip))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }
}
