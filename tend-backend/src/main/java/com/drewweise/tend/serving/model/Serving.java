
package com.drewweise.tend.serving.model;

import com.drewweise.tend.food.model.Food;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Represents one way of measuring a portion of a Food.
 *
 * Example: A banana might have multiple serving options:
 * - 1 medium banana (118 g)
 * - 100 grams
 * - 1 cup sliced (150 g)
 *
 * Each Serving belongs to one Food, but a Food
 * can have multiple Serving records.
 */
@Entity
@Table(name = "servings")
public class Serving {

    // Primary key generated automatically by MySQL.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serving_id")
    private Integer servingId;

    /**
     * The Food this serving belongs to.
     *
     * Many servings can reference the same Food.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    /**
     * Numerical amount represented by this serving.
     *
     * Example: 100 for "100 g".
     */
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    /**
     * Unit used to measure the serving.
     *
     * Examples: g, ml, cup, slice, whole.
     */
    @Column(name = "unit", nullable = false)
    private String unit;

    /**
     * Optional human-readable description.
     *
     * Examples:
     * "Medium banana", "One container", "Large slice".
     */
    @Column(name = "description")
    private String description;

    /**
     * Indicates whether this is the preferred serving
     * option displayed when a user selects the Food.
     */
    @Column(name = "default_serving", nullable = false)
    private boolean defaultServing;

    // Required by JPA/Hibernate.
    protected Serving() {
    }

    /**
     * Creates a serving definition for a Food.
     */
    public Serving(
            Food food,
            BigDecimal quantity,
            String unit,
            String description,
            boolean defaultServing
    ) {
        if (food == null) {
            throw new IllegalArgumentException(
                    "Food cannot be null"
            );
        }

        this.food = food;
        setQuantity(quantity);
        setUnit(unit);
        this.description = description;
        this.defaultServing = defaultServing;
    }

    // GETTERS

    public Integer getServingId() {
        return servingId;
    }

    public Food getFood() {
        return food;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDefaultServing() {
        return defaultServing;
    }

    // SETTERS

    public void setQuantity(BigDecimal quantity) {
        if (quantity == null ||
                quantity.compareTo(BigDecimal.ZERO) <= 0) {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefaultServing(boolean defaultServing) {
        this.defaultServing = defaultServing;
    }
}
