package com.raj.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="cart_items")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "product_id",unique = true)
    private Product product;
    private Integer qty;
    private java.math.BigDecimal priceSnapshot;
    private boolean isInStock;
    private LocalDateTime createdTs;
    private LocalDateTime updatedTs;
    // getters/setters
}
