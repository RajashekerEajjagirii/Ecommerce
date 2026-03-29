package com.raj.ecommerce.service;

import com.raj.ecommerce.constants.Constants;
import com.raj.ecommerce.domain.Order;
import com.raj.ecommerce.domain.Shipment;
import com.raj.ecommerce.domain.ShipmentTracker;
import com.raj.ecommerce.exception.BadRequestException;
import com.raj.ecommerce.repo.ShipmentRepository;
import com.raj.ecommerce.util.NumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepo;

    @Autowired
    private NumberGenerator generator;

    public Shipment createShipment(Order order) {
        try {
            String trackingNumber = "RETN" + generator.generate12DigitNumber();

            Shipment shipment = new Shipment();
            shipment.setOrder(order);
            shipment.setCarrier(Constants.CARRIER_TYPE);
            shipment.setTrackingNumber(trackingNumber);
            shipment.setStatus("CREATED");
            shipment.setCreatedAt(new Date().toInstant());
            shipment.setExpectedDelivery(LocalDateTime.now());

            // creating initial tracking event
            ShipmentTracker trackEvent = new ShipmentTracker();
            trackEvent.setEventType("Shipment Created");
            trackEvent.setEventTime(LocalDateTime.now());
            trackEvent.setTrackingNumber(trackingNumber);
            trackEvent.setLocation("Warehouse");
            trackEvent.setRemarks("Tracking number assigned: " + trackingNumber);

            // Add tracker to shipment (sets FK automatically)
            shipment.addTrackingEvent(trackEvent);
            // saving to db
            shipmentRepo.save(shipment);

            return shipment;
        } catch (Exception e) {
            throw new BadRequestException("Exception Occurred while preparing shipment: "+e);
        }
    }
}
