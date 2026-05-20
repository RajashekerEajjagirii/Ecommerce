package com.raj.ecommerce.domain.primary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(exclude = "shipment")
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
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Shipment shipment;
    private LocalDateTime lastUpdated_ts;
    // getters/setters
}


