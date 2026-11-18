
package com.drewweise.tend.kitchen.repository;

import com.drewweise.tend.kitchen.model.KitchenItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Handles database operations for Foods saved
 * to a user's Kitchen.
 */
public interface KitchenItemRepository
        extends JpaRepository<KitchenItem, Integer> {

    /**
     * Retrieves a user's KitchenItems and their Foods.
     *
     * EntityGraph tells Hibernate to fetch Food
     * with each KitchenItem, avoiding N+1 queries.
     */
    @EntityGraph(attributePaths = {"food"})
    List<KitchenItem> findByUser_UserId(Integer userId);

    /**
     * Checks whether a Food is already saved.
     */
    boolean existsByUser_UserIdAndFood_FoodId(
            Integer userId,
            Integer foodId
    );

    /**
     * Finds a KitchenItem only if it belongs
     * to the specified user.
     *
     * Useful for secure deletion.
     */
    Optional<KitchenItem> findByKitchenItemIdAndUser_UserId(
            Integer kitchenItemId,
            Integer userId
    );
}
