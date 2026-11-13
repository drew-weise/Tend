package com.drewweise.tend.nutrition.model;

import com.drewweise.tend.food.model.Food;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "nutrition")
public class Nutrition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutrition_id")
    private Integer nutritionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false, unique = true)
    private Food food;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private NutritionSource source;

    @Column(name = "calories")
    private BigDecimal calories;

    @Column(name = "protein")
    private BigDecimal protein;

    @Column(name = "carbs")
    private BigDecimal carbs;

    @Column(name = "fat")
    private BigDecimal fat;

    protected Nutrition() {
    }

    public Nutrition(Food food, NutritionSource source) {
        if (food == null || source == null) {
            throw new IllegalArgumentException(
                    "Food and nutrition source cannot be null"
            );
        }

        this.food = food;
        this.source = source;
    }

    public Integer getNutritionId() {
        return nutritionId;
    }

    public Food getFood() {
        return food;
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

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }
}