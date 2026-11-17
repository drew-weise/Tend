
package com.drewweise.tend.preparation.repository;

import com.drewweise.tend.preparation.model.FoodPreparation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Handles database operations for FoodPreparation.
 *
 * Because FoodPreparation shares its primary key
 * with Food, we can retrieve preparation details
 * directly using the Food's ID.
 */
public interface FoodPreparationRepository
        extends JpaRepository<FoodPreparation, Integer> {

}
