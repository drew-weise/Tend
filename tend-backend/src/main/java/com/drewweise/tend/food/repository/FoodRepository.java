
package com.drewweise.tend.food.repository;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.food.dto.FoodSearchResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    /** Recipe ingredient search only returns existing shared or owned Tend Foods. */
    @Query("""
            select new com.drewweise.tend.food.dto.FoodSearchResult(f.foodId, f.name)
            from Food f
            where (f.user is null or f.user.userId = :userId)
              and locate(lower(:query), lower(f.name)) > 0
            order by case when lower(f.name) = lower(:query) then 0 else 1 end,
                     length(f.name), f.name, f.foodId
            """)
    List<FoodSearchResult> searchIngredients(
            @Param("query") String query,
            @Param("userId") Integer userId,
            Pageable pageable);
}
