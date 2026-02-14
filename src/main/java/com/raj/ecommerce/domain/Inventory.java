package com.raj.ecommerce.domain;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private String warehouse;
    private Integer quantity;
    @Version
    private Integer version;
    // getters/setters
}
