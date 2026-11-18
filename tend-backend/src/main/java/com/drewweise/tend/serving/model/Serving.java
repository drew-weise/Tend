
package com.drewweise.tend.serving.model;

import com.drewweise.tend.food.model.Food;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a portion of a food, such as 100 g,
 * 1 medium banana, or 1 cup of sliced banana.
 *
 * A food can have multiple serving options.
 */
@Entity
@Table(name = "servings")
@Getter
public class Serving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serving_id")
    private Integer servingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Setter
    @Column(name = "description")
    private String description;

    @Setter
    @Column(name = "default_serving", nullable = false)
    private boolean defaultServing;

    // Weight in grams, used for nutrition calculations.
    @Setter
    @Column(name = "gram_weight", precision = 12, scale = 3)
    private BigDecimal gramWeight;

    protected Serving() {
    }

    public Serving(
            Food food,
            BigDecimal quantity,
            String unit,
            String description,
            boolean defaultServing
    ) {
        if (food == null) {
            throw new IllegalArgumentException("Food cannot be null");
        }

        this.food = food;
        setQuantity(quantity);
        setUnit(unit);
        this.description = description;
        this.defaultServing = defaultServing;
    }

    public void setQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Serving quantity must be greater than zero"
            );
        }

        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException(
                    "Serving unit cannot be blank"
            );
        }

        this.unit = unit;
    }
}
