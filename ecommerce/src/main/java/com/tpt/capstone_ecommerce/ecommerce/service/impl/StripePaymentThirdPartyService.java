package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentStatus;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RefundResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import com.tpt.capstone_ecommerce.ecommerce.entity.OrderItem;
import com.tpt.capstone_ecommerce.ecommerce.entity.Sku;
import com.tpt.capstone_ecommerce.ecommerce.enums.CURRENCY;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.service.PaymentThirdPartyService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Qualifier(value = "stripe")
public class StripePaymentThirdPartyService implements PaymentThirdPartyService {
    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.frontend-url}")
    private String frontEndUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request, Order order) {
        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontEndUrl + "order_id=" + order.getId())
                    .setCancelUrl(frontEndUrl + "order_id=" + order.getId())
                    .putMetadata("order_id", order.getId());

            for (OrderItem orderItem : order.getOrderItems()) {
                Sku sku = orderItem.getSku();

                builder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) orderItem.getQuantity()) // Lấy số lượng từ orderItem
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(request.getCurrency()) // Định dạng tiền tệ
                                                .setUnitAmountDecimal(BigDecimal.valueOf(sku.getPrice())) // Giá sản phẩm
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .putMetadata("app_id", sku.getId()) // Metadata để truy xuất
                                                                .setName(sku.getName()) // Tên sản phẩm hiển thị
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                );
            }

            SessionCreateParams params = builder.build();
            Session session = Session.create(params);

            return new PaymentResponse(session.getId(), PAYMENT_STATUS.PENDING.name(), session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException("Stripe payment failed: " + e.getMessage());
        }
    }

    @Override
    public PaymentStatus checkPaymentStatus(String sessionId) {
        try {
            Stripe.apiKey = stripeSecretKey;
            Session session = Session.retrieve(sessionId);
            String orderId = session.getMetadata().get("orderId");
            String paymentIntentId = session.getPaymentIntent(); // Transaction ID (PaymentIntent ID)
            String status = session.getPaymentStatus(); // Trạng thái thanh toán

            return new PaymentStatus(paymentIntentId, status, orderId);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to check payment status: " + e.getMessage());
        }
    }
}
