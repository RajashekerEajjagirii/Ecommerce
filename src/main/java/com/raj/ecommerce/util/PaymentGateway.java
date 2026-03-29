package com.raj.ecommerce.util;

import com.raj.ecommerce.dto.PaymentRequest;
import com.raj.ecommerce.dto.PaymentResponse;

public interface PaymentGateway {

    public PaymentResponse createPaymentForOrder(PaymentRequest paymentRequest) throws Exception;
}
