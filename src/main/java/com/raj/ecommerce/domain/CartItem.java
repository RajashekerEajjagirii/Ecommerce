package com.raj.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Data;

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
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer qty;
    private java.math.BigDecimal priceSnapshot;
    // getters/setters
}
