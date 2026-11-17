
package com.drewweise.tend.nutrition.repository;

import com.drewweise.tend.nutrition.model.Nutrition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Handles database operations for Nutrition entities.
 *
 * JpaRepository provides standard CRUD operations,
 * including save(), findById(), and deleteById().
 *
 * Each Food can have at most one Nutrition record.
 */
public interface NutritionRepository
        extends JpaRepository<Nutrition, Integer> {

    /**
     * Retrieves the nutritional information associated
     * with a specific Food.
     *
     * Spring Data JPA navigates the Nutrition.food
     * relationship and matches the Food's foodId.
     *
     * Optional is used because a Food might not
     * have nutritional information yet.
     */
    Optional<Nutrition> findByFood_FoodId(Integer foodId);
}
