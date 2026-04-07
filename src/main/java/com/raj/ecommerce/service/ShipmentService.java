package com.raj.ecommerce.service;

import com.raj.ecommerce.constants.Constants;
import com.raj.ecommerce.domain.Order;
import com.raj.ecommerce.domain.Shipment;
import com.raj.ecommerce.domain.ShipmentTracker;
import com.raj.ecommerce.domain.mongo.MongoOrder;
import com.raj.ecommerce.domain.mongo.ShipmentInfo;
import com.raj.ecommerce.dto.TrackerRequest;
import com.raj.ecommerce.dto.TrackerResponse;
import com.raj.ecommerce.exception.BadRequestException;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.exception.ServerDownException;
import com.raj.ecommerce.repo.ShipmentRepository;
import com.raj.ecommerce.repo.ShipmentTrackerRepo;
import com.raj.ecommerce.util.DomainConverter;
import com.raj.ecommerce.util.NumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepo;

    @Autowired
    private NumberGenerator generator;
    @Autowired
    private ShipmentTrackerRepo trackerRepo;
    @Autowired
    private DomainConverter converter;

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

    public TrackerResponse fetchTrackDetails(String trackingNumber) {
        try{
            ShipmentTracker trackInfo=trackerRepo.findFirstByTrackingNumberOrderByEventTimeDesc(trackingNumber)
                    .orElseThrow(()->new RecordNotFoundException("track details not found,please cross check ur trackingNumber"));
            return converter.trackerToTrackerResponse(trackInfo);

        } catch (Exception e) {
            throw new ServerDownException("Exception occurred while fetching track details "+e);
        }
    }

    public String updateTrackingStatus(TrackerRequest request) {
        try {
            Shipment shipment=shipmentRepo.findByTrackingNumber(request.getTrackingNumber())
                    .orElseThrow(()->new RecordNotFoundException("Shipment not found"));
            List<ShipmentTracker> trackerList=trackerRepo.findByTrackingNumber(request.getTrackingNumber());
            Optional<String> eventType = trackerList.stream().
                    map(ShipmentTracker::getEventType).filter(event->event.equalsIgnoreCase(request.getEventType())).findFirst();

            if(eventType.isEmpty()) {
                trackerRepo.save(
                        ShipmentTracker.builder()
                                .trackingNumber(request.getTrackingNumber())
                                .eventType(request.getEventType())
                                .location(request.getLocation())
                                .remarks(request.getRemarks())
                                .eventTime(LocalDateTime.now())
                                .shipment(shipment)
                                .build()
                );
                return "Event Tracking status updated successfully!";
            }
            return "Event Tracking status update we already had!";
        } catch (Exception e) {
            throw new BadRequestException("Exception occurred while updating tracking status "+e);
        }
    }
}
