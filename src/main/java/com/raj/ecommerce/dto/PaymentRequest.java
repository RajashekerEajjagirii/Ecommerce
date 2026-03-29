package com.raj.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private String name;
    private String currency;
    private BigDecimal amount;
    private String receipt;
    private Integer quantity;

}
