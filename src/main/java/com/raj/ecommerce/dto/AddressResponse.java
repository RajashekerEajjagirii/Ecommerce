package com.raj.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressResponse {

    private String line1;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
