
package com.drewweise.tend.food.repository;

import com.drewweise.tend.food.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Handles database operations for Food entities.
 *
 * JpaRepository automatically provides:
 * - save()
 * - findById()
 * - findAll()
 * - deleteById()
 * - existsById()
 *
 * Custom methods allow us to search for Foods
 * based on their properties and relationships.
 */
public interface FoodRepository extends JpaRepository<Food, Integer> {

    /**
     * Retrieves all Foods created by a specific user.
     *
     * Navigates the Food.user relationship
     * and matches the User's userId property.
     */
    List<Food> findByUser_UserId(Integer userId);

    /**
     * Retrieves shared/system Foods.
     *
     * These Foods have no associated user.
     */
    List<Food> findByUserIsNull();

    /**
     * Searches Foods by name, ignoring capitalization.
     *
     * Example:
     * findByNameContainingIgnoreCase("chicken")
     *
     * Matches:
     * - Chicken Breast
     * - Chicken Alfredo
     * - Grilled Chicken Sandwich
     */
    List<Food> findByNameContainingIgnoreCase(String name);
}
