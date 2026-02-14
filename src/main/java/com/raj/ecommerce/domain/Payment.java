package com.raj.ecommerce.domain;

import jakarta.persistence.*;

@Entity
@Table(name="payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name="order_id")
    private Order order;
    private String gatewayTxnId;
    private String status;
    private java.math.BigDecimal amount;
    private java.time.Instant createdAt = java.time.Instant.now();
    // getters/setters
}

