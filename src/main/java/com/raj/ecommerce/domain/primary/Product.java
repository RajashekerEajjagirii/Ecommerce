package com.raj.ecommerce.domain.primary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "category")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String sku;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private BigDecimal price;
    private Boolean active;
    @ManyToOne
    @JoinColumn(name = "category_id")
    /* If ur not using Response Dto*/
    @JsonBackReference //This tells Jackson to serialize only one side of the relationship (Child)
    private Category category;

}
