
package com.drewweise.tend.recipe.repository;

import com.drewweise.tend.recipe.model.Recipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Handles database operations for recipes
 * saved to a user's personal collection.
 */
public interface RecipeRepository
        extends JpaRepository<Recipe, Integer> {

    /**
     * Retrieves all recipes saved by a user.
     *
     * EntityGraph tells Hibernate to fetch the
     * associated Food records in the same query,
     * avoiding N+1 queries.
     */
    @EntityGraph(attributePaths = {"food"})
    List<Recipe> findByUser_UserId(Integer userId);

    /**
     * Checks whether a user has already saved
     * a particular recipe.
     */
    boolean existsByUser_UserIdAndFood_FoodId(
            Integer userId,
            Integer foodId
    );

    /**
     * Finds a saved recipe belonging to a specific user.
     *
     * Used to ensure users can only remove
     * recipes from their own collection.
     */
    Optional<Recipe> findBySavedRecipeIdAndUser_UserId(
            Integer savedRecipeId,
            Integer userId
    );
}
