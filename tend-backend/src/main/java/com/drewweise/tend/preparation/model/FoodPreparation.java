
package com.drewweise.tend.preparation.model;

import com.drewweise.tend.food.model.Food;
import jakarta.persistence.*;

/**
 * Stores preparation instructions and cooking information
 * for a Food.
 *
 * Examples:
 * - Homemade chicken Alfredo
 * - Frozen microwave dinner
 * - Scrambled eggs
 *
 * Not every Food requires preparation information,
 * so this is stored separately from Food.java.
 *
 * Each Food can have at most one FoodPreparation record.
 */
@Entity
@Table(name = "food_preparation")
public class FoodPreparation {

    /**
     * Uses the Food's ID as the primary key.
     *
     * @MapsId means FoodPreparation shares its
     * primary key with its associated Food.
     *
     * This enforces a one-to-one relationship.
     */
    @Id
    @Column(name = "food_id")
    private Integer foodId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "food_id")
    private Food food;

    // Time spent preparing ingredients before cooking.
    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    // Time spent actually cooking the food.
    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    /**
     * General preparation method.
     *
     * Examples:
     * "Stovetop", "Oven", "Microwave", "No Cook".
     */
    @Column(name = "preparation_method")
    private String preparationMethod;

    /**
     * Step-by-step preparation instructions.
     *
     * TEXT allows longer instructions than
     * a standard VARCHAR column.
     */
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    // Required by JPA/Hibernate.
    protected FoodPreparation() {
    }

    /**
     * Creates preparation information for a Food.
     *
     * The associated Food must already exist
     * or be persisted before this entity is saved.
     */
    public FoodPreparation(Food food) {
        if (food == null) {
            throw new IllegalArgumentException(
                    "Food cannot be null"
            );
        }

        this.food = food;
    }

    // GETTERS

    public Integer getFoodId() {
        return foodId;
    }

    public Food getFood() {
        return food;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public Integer getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public String getPreparationMethod() {
        return preparationMethod;
    }

    public String getInstructions() {
        return instructions;
    }

    /**
     * Convenience method for calculating total
     * preparation and cooking time.
     *
     * Returns null if either time is unknown.
     */
    @Transient
    public Integer getTotalTimeMinutes() {
        if (prepTimeMinutes == null || cookTimeMinutes == null) {
            return null;
        }

        return prepTimeMinutes + cookTimeMinutes;
    }

    // SETTERS

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

    public void setPreparationMethod(String preparationMethod) {
        this.preparationMethod = preparationMethod;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
