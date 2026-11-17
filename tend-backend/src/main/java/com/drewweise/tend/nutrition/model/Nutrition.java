
package com.drewweise.tend.nutrition.model;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.serving.model.Serving;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Stores nutritional information associated with a Food.
 *
 * Each Food can have at most one Nutrition record.
 *
 * Nutritional values are defined relative to a specific
 * Serving, allowing Tend to understand exactly what
 * quantity of food the values represent.
 *
 * Example:
 * Food: Banana
 * Reference Serving: 1 medium banana
 * Calories: 105
 * Protein: 1.3 g
 * Carbs: 27 g
 * Fat: 0.4 g
 *
 * Nutrition sources:
 * - MANUAL: Entered by a user
 * - REFERENCE: Retrieved from a nutritional database
 * - CALCULATED: Derived from the Food's ingredients
 *
 * Nutritional values may be null when unknown,
 * but cannot be negative.
 */
@Entity
@Table(name = "nutrition")
public class Nutrition {

    // Primary key generated automatically by MySQL.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutrition_id")
    private Integer nutritionId;

    /**
     * The Food associated with this Nutrition record.
     *
     * Each Food can have at most one Nutrition record.
     *
     * The unique constraint prevents multiple Nutrition
     * records from referencing the same Food.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false, unique = true)
    private Food food;

    /**
     * The Serving that defines the reference quantity
     * for all nutritional values in this record.
     *
     * Example:
     * If referenceServing represents 170 g of yogurt,
     * calories = 100 means 100 calories per 170 g.
     *
     * Many Nutrition records could theoretically reference
     * the same Serving, although each Food has only one
     * Nutrition record.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_serving_id", nullable = false)
    private Serving referenceServing;

    /**
     * Identifies where the nutritional information
     * originated.
     *
     * EnumType.STRING stores the enum name
     * instead of its numeric position.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private NutritionSource source;

    /**
     * Nutritional values for the reference Serving.
     *
     * Calories are measured in kcal.
     * Protein, carbohydrates, and fat are measured in grams.
     *
     * BigDecimal avoids floating-point precision issues.
     *
     * Null indicates that a value is unknown.
     */
    @Column(name = "calories")
    private BigDecimal calories;

    @Column(name = "protein")
    private BigDecimal protein;

    @Column(name = "carbs")
    private BigDecimal carbs;

    @Column(name = "fat")
    private BigDecimal fat;

    // Required by JPA/Hibernate.
    protected Nutrition() {
    }

    /**
     * Creates nutritional information for a Food.
     *
     * The reference Serving must belong to the same Food.
     *
     * Nutritional values can be populated afterward
     * using the setter methods.
     */
    public Nutrition(
            Food food,
            Serving referenceServing,
            NutritionSource source
    ) {
        if (food == null || referenceServing == null || source == null) {
            throw new IllegalArgumentException(
                    "Food, reference serving, and nutrition source cannot be null"
            );
        }

        // Prevent associating nutrition with a serving
        // that belongs to a different Food.
        if (referenceServing.getFood() != food &&
                (food.getFoodId() == null ||
                        !food.getFoodId().equals(
                                referenceServing.getFood().getFoodId()
                        ))) {
            throw new IllegalArgumentException(
                    "Reference serving must belong to the same Food"
            );
        }

        this.food = food;
        this.referenceServing = referenceServing;
        this.source = source;
    }

    // GETTERS

    public Integer getNutritionId() {
        return nutritionId;
    }

    public Food getFood() {
        return food;
    }

    public Serving getReferenceServing() {
        return referenceServing;
    }

    public NutritionSource getSource() {
        return source;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public BigDecimal getCarbs() {
        return carbs;
    }

    public BigDecimal getFat() {
        return fat;
    }

    // SETTERS

    /**
     * Updates calories (kcal).
     */
    public void setCalories(BigDecimal calories) {
        validateNonNegative(calories, "Calories");
        this.calories = calories;
    }

    /**
     * Updates protein (grams).
     */
    public void setProtein(BigDecimal protein) {
        validateNonNegative(protein, "Protein");
        this.protein = protein;
    }

    /**
     * Updates carbohydrates (grams).
     */
    public void setCarbs(BigDecimal carbs) {
        validateNonNegative(carbs, "Carbs");
        this.carbs = carbs;
    }

    /**
     * Updates fat (grams).
     */
    public void setFat(BigDecimal fat) {
        validateNonNegative(fat, "Fat");
        this.fat = fat;
    }

    /**
     * Validates nutritional values before assignment.
     *
     * null = Unknown value (allowed)
     * 0    = Known value of zero (allowed)
     * < 0  = Invalid value (rejected)
     *
     * BigDecimal.compareTo() compares numeric values
     * without converting to a primitive type.
     */
    private void validateNonNegative(
            BigDecimal value,
            String fieldName
    ) {
        if (value != null &&
                value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be negative"
            );
        }
    }
}
