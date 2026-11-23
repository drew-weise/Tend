
package com.drewweise.tend.recipe.model;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Represents a recipe saved to a user's collection.
 *
 * Multiple users can save the same shared recipe
 * without duplicating its underlying Food record.
 */
@Entity
@Table(
        name = "saved_recipes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_saved_recipe_user_food",
                        columnNames = {"user_id", "food_id"}
                )
        }
)
@Getter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_recipe_id")
    private Integer savedRecipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    protected Recipe() {
    }

    public Recipe(User user, Food food) {
        if (user == null || food == null) {
            throw new IllegalArgumentException(
                    "User and Food cannot be null"
            );
        }

        this.user = user;
        this.food = food;
    }
}
