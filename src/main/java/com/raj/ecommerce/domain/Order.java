package com.raj.ecommerce.domain;

import jakarta.persistence.*;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    private java.math.BigDecimal totalAmount;
    private String status;
    private java.time.Instant createdAt = java.time.Instant.now();
    // getters/setters
}


