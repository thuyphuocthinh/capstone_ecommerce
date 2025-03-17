package com.tpt.capstone_ecommerce.ecommerce.service.factory;

import com.tpt.capstone_ecommerce.ecommerce.service.PaymentThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentServiceFactory {

    private final Map<String, PaymentThirdPartyService> paymentServices;

    @Autowired
    public PaymentServiceFactory(List<PaymentThirdPartyService> paymentServiceList) {
        this.paymentServices = paymentServiceList.stream()
                .collect(Collectors.toMap(
                        service -> service.getClass().getSimpleName().replace("PaymentThirdPartyService", "").toUpperCase(),
                        service -> service
                ));
    }

    public PaymentThirdPartyService getPaymentService(String method) {
        PaymentThirdPartyService service = paymentServices.get(method.toUpperCase());
        if (service == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        return service;
    }
}
