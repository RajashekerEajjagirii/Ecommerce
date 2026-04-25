package com.raj.ecommerce.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString(exclude = "order")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Order order;

    private String carrier;
    private String trackingNumber;
    private String status;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    private LocalDateTime expectedDelivery;

    @OneToMany(mappedBy = "shipment",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ShipmentTracker> trackingEvents=new ArrayList<>();

    // Helper method to add a tracking event
    public void addTrackingEvent(ShipmentTracker tracker){
        tracker.setShipment(this);
        this.trackingEvents.add(tracker);
    }

}
