package com.raj.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String name;

    // One category can have many products
     @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
     /* If ur not using Response Dto*/
     @JsonManagedReference //This tells Jackson to serialize only one side of the relationship (Parent)
     private Set<Product> products = new HashSet<>();
}
