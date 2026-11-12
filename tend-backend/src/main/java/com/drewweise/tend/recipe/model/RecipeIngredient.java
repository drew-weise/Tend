
package com.drewweise.tend.recipe.model;

import com.drewweise.tend.food.Food;
import jakarta.persistence.*;

@Entity
@Table(name = "recipe_ingredient")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ingredient_id")
    private int recipeIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "amount", nullable = false)
    private String amount;

    public RecipeIngredient() {
    }

    public RecipeIngredient(Recipe recipe, Food food, String amount) {
        this.recipe = recipe;
        this.food = food;
        this.amount = amount;
    }

    public int getRecipeIngredientId() {
        return recipeIngredientId;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "recipeIngredientId=" + recipeIngredientId +
                ", foodId=" + (food != null ? food.getFoodId() : null) +
                ", amount='" + amount + '\'' +
                '}';
    }
}
