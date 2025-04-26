package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.OrderErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.PaymenErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.PaymentErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RetryPaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaymentResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RetryPaymentResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import com.tpt.capstone_ecommerce.ecommerce.entity.Payment;
import com.tpt.capstone_ecommerce.ecommerce.enums.CURRENCY;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_METHOD;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_THIRD_PARTIES;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.OrderRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.PaymentRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.PaymentService;
import com.tpt.capstone_ecommerce.ecommerce.service.factory.PaymentServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentServiceFactory paymentServiceFactory;

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    public PaymentServiceImpl(PaymentServiceFactory paymentServiceFactory, PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentServiceFactory = paymentServiceFactory;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest, String orderId, String paymentThirdParty) throws Exception {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));
        Payment payment = Payment.builder()
                .paymentMethod(PAYMENT_METHOD.BANKING)
                .order(order)
                .totalPrice(order.getTotalPrice())
                .build();
        this.paymentRepository.save(payment);
        return this.paymentServiceFactory.getPaymentService(paymentThirdParty).createPayment(paymentRequest, order);
    }

    @Override
    public void updatePaymentStatusAndTransactionIdByOrderId(String orderId, String status, String transactionId) throws NotFoundException {
        this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));
        Payment payment = this.paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(PaymentErrorConstant.PAYMENT_NOT_FOUND));
        payment.setPaymentStatus(PAYMENT_STATUS.valueOf(status));
        payment.setTransactionId(transactionId);
        this.paymentRepository.save(payment);
    }

    @Override
    public String getPaymentStatus(String orderId) throws NotFoundException {
        this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));
        Payment payment = this.paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(PaymentErrorConstant.PAYMENT_NOT_FOUND));
        return payment.getPaymentStatus().toString();
    }

    @Override
    public void createPaymentCash(String orderId) {
        log.info("order id {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));
        Payment payment = Payment.builder()
                .paymentMethod(PAYMENT_METHOD.CASH)
                .order(order)
                .totalPrice(order.getTotalPrice())
                .build();
        this.paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentCash(Order order) throws NotFoundException {
        Payment payment = this.paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new NotFoundException(PaymentErrorConstant.PAYMENT_NOT_FOUND));

        payment.setPaymentStatus(PAYMENT_STATUS.SUCCESS);
        this.paymentRepository.save(payment);
    }

    @Override
    public RetryPaymentResponse retryOnlinePaymentHandler(RetryPaymentRequest request, String ipAddress) throws Exception {
        String paymentThirdParty = request.getPaymentThirdParty().name();

        Order order = this.orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));
        PaymentResponse paymentResponse = this.paymentServiceFactory.getPaymentService(paymentThirdParty).createPayment(
                new PaymentRequest(BigDecimal.valueOf(order.getFinalTotalPrice()), CURRENCY.VND.name(), ipAddress),
                order
        );
        return RetryPaymentResponse.builder()
                .redirectUrl(paymentResponse.getRedirectUrl())
                .build();
    }

    @Override
    public String updatePaymentStatusByOrderId(String orderId) throws NotFoundException {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));
        Payment findByOrderId = this.paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(PaymentErrorConstant.PAYMENT_NOT_FOUND));
        findByOrderId.setPaymentStatus(PAYMENT_STATUS.SUCCESS);
        this.paymentRepository.save(findByOrderId);
        return "Success";
    }
}
