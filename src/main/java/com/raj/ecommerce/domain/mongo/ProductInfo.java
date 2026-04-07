package com.raj.ecommerce.domain.mongo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductInfo {

    private long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
