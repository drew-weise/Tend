
package com.drewweise.tend.food.repository;

import com.drewweise.tend.food.model.FoodIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Handles database operations for FoodIngredient entities.
 *
 * JpaRepository provides common operations automatically:
 * - save()
 * - findById()
 * - findAll()
 * - delete()
 *
 * Custom query methods allow us to retrieve ingredients
 * belonging to a specific Food.
 */
public interface FoodIngredientRepository
        extends JpaRepository<FoodIngredient, Integer> {

    /**
     * Retrieves all ingredients belonging to a Food.
     *
     * Spring Data JPA interprets the method name:
     *
     * findBy        -> Search for matching records
     * ParentFood    -> Follow the parentFood relationship
     * FoodId        -> Match the Food's foodId property
     *
     * Example:
     * findByParentFood_FoodId(5)
     *
     * Returns all FoodIngredient records where
     * parentFood.foodId == 5.
     *
     * No SQL query needs to be written manually.
     */
    List<FoodIngredient> findByParentFood_FoodId(Integer foodId);
}
