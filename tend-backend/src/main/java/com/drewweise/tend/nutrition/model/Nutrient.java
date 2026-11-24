
package com.drewweise.tend.nutrition.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "nutrients")
@Getter
public class Nutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutrient_id")
    private Integer nutrientId;

    // Original USDA nutrient identifier.
    @Column(name = "usda_nutrient_id", unique = true)
    private Integer usdaNutrientId;

    @Column(name = "name", nullable = false)
    private String name;

    // Unit used to measure this nutrient (g, mg, µg, kcal).
    @Column(name = "unit", nullable = false)
    private String unit;

    // Required by JPA/Hibernate.
    protected Nutrient() {
    }

    public Nutrient(
            Integer usdaNutrientId,
            String name,
            String unit
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Nutrient name cannot be blank"
            );
        }

        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException(
                    "Nutrient unit cannot be blank"
            );
        }

        this.usdaNutrientId = usdaNutrientId;
        this.name = name;
        this.unit = unit;
    }
}
