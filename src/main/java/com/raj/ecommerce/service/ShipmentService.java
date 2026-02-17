package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.Order;
import com.raj.ecommerce.domain.Shipment;
import com.raj.ecommerce.repo.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepo;

    public Shipment createShipment(Order order) {

        return Shipment.builder()
                .order(order)
                .carrier("FedEx")
                .trackingNumber(UUID.randomUUID().toString())
                .status("CREATED")
                .createdAt(new Date().toInstant())
                .build();
    }
}
