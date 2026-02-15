package com.raj.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartItemResponse {

    private Long id;
    private String productName;
    private int qty;
    private BigDecimal priceSnapshot;
}
