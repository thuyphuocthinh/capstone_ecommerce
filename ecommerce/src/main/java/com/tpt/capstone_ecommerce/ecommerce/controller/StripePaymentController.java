package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stripe")
public class StripePaymentController {
    private final PaymentService paymentService;

    public StripePaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        String endpointSecret = "sk_webhook_secret";
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;

                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;

                default:
                    break;
            }

            return ResponseEntity.ok("Webhook received");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook signature error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook JSON parsing error: " + e.getMessage());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) throws JsonProcessingException {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isPresent()) {
            Session session = (Session) dataObjectDeserializer.getObject().get();
            String orderId = session.getMetadata().get("order_id");
            String transactionId = session.getPaymentIntent();
            paymentService.updatePaymentStatusAndTransactionIdByOrderId(orderId, PAYMENT_STATUS.SUCCESS.name(), transactionId);
        }
    }

    private void handlePaymentFailed(Event event) throws JsonProcessingException {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isPresent()) {
            PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
            String failedOrderId = paymentIntent.getMetadata().get("order_id");
            paymentService.updatePaymentStatusAndTransactionIdByOrderId(failedOrderId, PAYMENT_STATUS.FAILED.name(), null);
        }
    }

}
