package com.raj.ecommerce.domain.mongo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentInfo {

    private String paymentId;
    private String method; // e.g., "Stripe", "Razorpay"
    private String status; // e.g., "PAID", "PENDING"
    private BigDecimal amount;
}
