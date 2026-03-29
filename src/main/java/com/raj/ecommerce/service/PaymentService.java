package com.raj.ecommerce.service;

import com.raj.ecommerce.dto.PaymentRequest;
import com.raj.ecommerce.dto.PaymentResponse;
import com.raj.ecommerce.util.PaymentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentService {

    private final PaymentGateway gateway;

    public PaymentService(Map<String, PaymentGateway> gateways,
                          @Value("${payment.provider}") String provider) {
        this.gateway = gateways.get(provider + "Gateway");
        if (this.gateway == null) {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    public PaymentResponse processPayment(PaymentRequest paymentRequest) throws Exception {
        return gateway.createPaymentForOrder(paymentRequest);
    }
}

