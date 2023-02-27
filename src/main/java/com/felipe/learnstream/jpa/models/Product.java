package com.felipe.learnstream.jpa.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    @With
    private Double price;

    @ManyToMany(mappedBy = "products")
//    @ToString.Exclude
    private Set<Order> orders;
}
