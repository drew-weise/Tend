
package com.drewweise.tend.food.model;

import com.drewweise.tend.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents anything edible in Tend, including ingredients,
 * packaged products, and homemade meals.
 *
 * Nutrition, servings, ingredients, and preparation
 * instructions are managed by separate entities.
 */
@Entity
@Table(name = "foods")
@Getter
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Integer foodId;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Null for shared or system foods.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // References the original food when a user creates a copy.
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_food_id")
    private Food sourceFood;

    // Number of portions produced by this food.
    @Column(name = "yield_servings", nullable = false)
    private BigDecimal yieldServings = BigDecimal.ONE;

    // Original USDA identifier, if imported from FoodData Central.
    @Setter
    @Column(name = "usda_fdc_id", unique = true)
    private Long usdaFdcId;

    // Required by JPA/Hibernate.
    protected Food() {
    }

    public Food(String name, User user) {
        this.name = name;
        this.user = user;
    }

    // Custom setter preserves validation.
    public void setYieldServings(BigDecimal yieldServings) {
        if (yieldServings == null ||
                yieldServings.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Yield servings must be greater than zero"
            );
        }

        this.yieldServings = yieldServings;
    }
}
