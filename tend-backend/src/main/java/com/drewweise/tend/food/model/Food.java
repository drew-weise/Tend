package com.drewweise.tend.food.model;

import com.drewweise.tend.user.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Integer foodId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_food_id")
    private Food sourceFood;

    @Column(name = "servings", nullable = false)
    private BigDecimal servings = BigDecimal.ONE;

    @Column(name = "serving_description")
    private String servingDescription;

    protected Food() {
    }

    public Food(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }

    public Food getSourceFood() {
        return sourceFood;
    }

    public BigDecimal getServings() {
        return servings;
    }

    public String getServingDescription() {
        return servingDescription;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSourceFood(Food sourceFood) {
        this.sourceFood = sourceFood;
    }

    public void setServings(BigDecimal servings) {
        this.servings = servings;
    }

    public void setServingDescription(String servingDescription) {
        this.servingDescription = servingDescription;
    }
}