package com.raj.ecommerce.controller;

import com.raj.ecommerce.dto.TrackerRequest;
import com.raj.ecommerce.dto.TrackerResponse;
import com.raj.ecommerce.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ShipmentTrackerController {

    @Autowired
    private ShipmentService shipmentService;

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<TrackerResponse> fetchTrackingDetails(@PathVariable String trackingNumber){
       return new ResponseEntity<>(shipmentService.fetchTrackDetails(trackingNumber), HttpStatus.OK);
    }

    @PostMapping("/tracking/updateStatus")
    public ResponseEntity<String> updateTrackingStatus(@RequestBody TrackerRequest request){
        return new ResponseEntity<>(shipmentService.updateTrackingStatus(request),HttpStatus.OK);
    }
}
