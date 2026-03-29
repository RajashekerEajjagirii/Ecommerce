package com.raj.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TrackerRequest {
    private String trackingNumber;
    private String eventType;
    private String location;
    private String remarks;
}
