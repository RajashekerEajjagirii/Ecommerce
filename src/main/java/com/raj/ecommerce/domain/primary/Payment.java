package com.raj.ecommerce.domain.primary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="payments")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
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

