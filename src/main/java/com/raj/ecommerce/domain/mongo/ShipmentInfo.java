package com.raj.ecommerce.domain.mongo;

import lombok.Data;

@Data
public class ShipmentInfo {

    private Long shipmentId;
    private String carrier;
    private String trackingNumber;
    private String status; // e.g., "SHIPPED", "DELIVERED"
}
