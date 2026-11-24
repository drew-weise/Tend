
package com.drewweise.tend.nutrition.repository;

import com.drewweise.tend.nutrition.model.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing nutrient definitions.
 *
 * Nutrients describe individual nutritional components
 * such as protein, fat, carbohydrates, and vitamins.
 *
 * Nutrient amounts associated with specific foods
 * are managed through FoodNutrient.
 */
public interface NutrientRepository extends JpaRepository<Nutrient, Integer> {

    /**
     * Finds a nutrient by its name.
     */
    Optional<Nutrient> findByName(String name);

    /**
     * Finds a nutrient using its original USDA identifier.
     *
     * Useful when importing or synchronizing USDA data.
     */
    Optional<Nutrient> findByUsdaNutrientId(Integer usdaNutrientId);
}
