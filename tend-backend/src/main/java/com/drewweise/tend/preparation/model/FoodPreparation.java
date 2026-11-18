
package com.drewweise.tend.preparation.model;

import com.drewweise.tend.food.model.Food;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores optional preparation information for a Food,
 * including cooking times, methods, and instructions.
 *
 * Each Food can have at most one FoodPreparation record.
 */
@Entity
@Table(name = "food_preparation")
@Getter
public class FoodPreparation {

    // Shares its primary key with the associated Food.
    @Id
    @Column(name = "food_id")
    private Integer foodId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    @Setter
    @Column(name = "preparation_method")
    private String preparationMethod;

    @Setter
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    // Required by JPA/Hibernate.
    protected FoodPreparation() {
    }

    public FoodPreparation(Food food) {
        if (food == null) {
            throw new IllegalArgumentException("Food cannot be null");
        }

        this.food = food;
    }

    /**
     * Returns total preparation and cooking time,
     * or null if either value is unknown.
     */
    @Transient
    public Integer getTotalTimeMinutes() {
        if (prepTimeMinutes == null || cookTimeMinutes == null) {
            return null;
        }

        return prepTimeMinutes + cookTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        if (prepTimeMinutes != null && prepTimeMinutes < 0) {
            throw new IllegalArgumentException(
                    "Preparation time cannot be negative"
            );
        }

        this.prepTimeMinutes = prepTimeMinutes;
    }

    public void setCookTimeMinutes(Integer cookTimeMinutes) {
        if (cookTimeMinutes != null && cookTimeMinutes < 0) {
            throw new IllegalArgumentException(
                    "Cooking time cannot be negative"
            );
        }

        this.cookTimeMinutes = cookTimeMinutes;
    }
}
