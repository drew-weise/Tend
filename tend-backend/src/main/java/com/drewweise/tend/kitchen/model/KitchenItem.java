
package com.drewweise.tend.kitchen.model;

import com.drewweise.tend.food.Food;
import com.drewweise.tend.recipe.model.Recipe;
import com.drewweise.tend.user.model.User;
import jakarta.persistence.*;

@Entity
@Table(
        name = "kitchen_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_kitchen_user_food",
                        columnNames = {"user_id", "food_id"}
                ),
                @UniqueConstraint(
                        name = "uk_kitchen_user_recipe",
                        columnNames = {"user_id", "recipe_id"}
                )
        }
)
public class KitchenItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kitchen_item_id")
    private int kitchenItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    // Required by Hibernate
    protected KitchenItem() {
    }

    // Save a food to someone's Kitchen
    public KitchenItem(User user, Food food) {
        if (user == null || food == null) {
            throw new IllegalArgumentException(
                    "User and food cannot be null"
            );
        }

        this.user = user;
        this.food = food;
    }

    // Save a recipe to someone's Kitchen
    public KitchenItem(User user, Recipe recipe) {
        if (user == null || recipe == null) {
            throw new IllegalArgumentException(
                    "User and recipe cannot be null"
            );
        }

        this.user = user;
        this.recipe = recipe;
    }

    public int getKitchenItemId() {
        return kitchenItemId;
    }

    public User getUser() {
        return user;
    }

    public Food getFood() {
        return food;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    @Override
    public String toString() {
        return "KitchenItem{" +
                "kitchenItemId=" + kitchenItemId +
                ", userId=" +
                (user != null ? user.getUserId() : null) +
                ", foodId=" +
                (food != null ? food.getFoodId() : null) +
                ", recipeId=" +
                (recipe != null ? recipe.getRecipeId() : null) +
                '}';
    }
}
