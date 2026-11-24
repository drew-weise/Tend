
package com.drewweise.tend.nutrition.model;

import com.drewweise.tend.food.model.Food;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "food_nutrients",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"food_id", "nutrient_id"}
        )
)
@Getter
public class FoodNutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_nutrient_id")
    private Integer foodNutrientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrient_id", nullable = false)
    private Nutrient nutrient;

    // Nutrient amount per 100 grams of food.
    @Column(
            name = "amount_per_100g",
            nullable = false,
            precision = 14,
            scale = 6
    )
    private BigDecimal amountPer100g;

    // Required by JPA/Hibernate.
    protected FoodNutrient() {
    }

    public FoodNutrient(
            Food food,
            Nutrient nutrient,
            BigDecimal amountPer100g
    ) {
        if (food == null || nutrient == null) {
            throw new IllegalArgumentException(
                    "Food and nutrient cannot be null"
            );
        }

        this.food = food;
        this.nutrient = nutrient;
        setAmountPer100g(amountPer100g);
    }

    public void setAmountPer100g(BigDecimal amountPer100g) {
        if (amountPer100g == null ||
                amountPer100g.signum() < 0) {
            throw new IllegalArgumentException(
                    "Nutrient amount cannot be null or negative"
            );
        }

        this.amountPer100g = amountPer100g;
    }
}
