
package com.drewweise.tend.nutrition.model;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.serving.model.Serving;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Stores nutritional information for a Food relative to
 * a reference Serving.
 *
 * Each Food has at most one Nutrition record.
 * Unknown nutritional values may be null, but never negative.
 */
@Entity
@Table(name = "nutrition")
@Getter
public class Nutrition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutrition_id")
    private Integer nutritionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false, unique = true)
    private Food food;

    // Serving that defines the quantity for nutritional values.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_serving_id", nullable = false)
    private Serving referenceServing;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private NutritionSource source;

    // Calories in kcal; macronutrients in grams.
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

        // The reference serving must belong to this Food.
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

    // Custom setters preserve nutrient validation.

    public void setCalories(BigDecimal calories) {
        validateNonNegative(calories, "Calories");
        this.calories = calories;
    }

    public void setProtein(BigDecimal protein) {
        validateNonNegative(protein, "Protein");
        this.protein = protein;
    }

    public void setCarbs(BigDecimal carbs) {
        validateNonNegative(carbs, "Carbs");
        this.carbs = carbs;
    }

    public void setFat(BigDecimal fat) {
        validateNonNegative(fat, "Fat");
        this.fat = fat;
    }

    private void validateNonNegative(BigDecimal value, String fieldName) {
        if (value != null && value.signum() < 0) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be negative"
            );
        }
    }
}
