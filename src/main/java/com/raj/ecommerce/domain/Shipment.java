package com.raj.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "shipments")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String carrier;
    private String trackingNumber;
    private String status;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

}
