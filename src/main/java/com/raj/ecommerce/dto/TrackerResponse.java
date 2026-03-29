package com.raj.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TrackerResponse {
    private String trackingNumber;
    private String eventType;
    private String location;
    private String remarks;
    private LocalDateTime eventTime;
}
