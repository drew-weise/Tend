
package com.drewweise.tend.kitchen.model;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.user.model.User;
import jakarta.persistence.*;

/**
 * Represents a Food saved to a User's Kitchen.
 *
 * KitchenItem connects a User to a Food without
 * duplicating the Food's actual information.
 *
 * Example:
 * User: Drew
 * Food: Chicken Alfredo
 *
 * Multiple users can save the same Food to their Kitchens.
 */
@Entity
@Table(
        name = "kitchen_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_kitchen_user_food",
                        columnNames = {"user_id", "food_id"}
                )
        }
)
public class KitchenItem {

    // Primary key generated automatically by MySQL.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kitchen_item_id")
    private Integer kitchenItemId;

    /**
     * The User who saved this Food.
     *
     * One User can have many KitchenItems.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The Food saved in the User's Kitchen.
     *
     * Many users can save the same Food.
     *
     * The unique constraint above prevents the
     * same User from saving the same Food twice.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    // Required by JPA/Hibernate.
    protected KitchenItem() {
    }

    /**
     * Creates a KitchenItem connecting a User to a Food.
     */
    public KitchenItem(User user, Food food) {
        if (user == null || food == null) {
            throw new IllegalArgumentException(
                    "User and Food cannot be null"
            );
        }

        this.user = user;
        this.food = food;
    }

    // GETTERS

    public Integer getKitchenItemId() {
        return kitchenItemId;
    }

    public User getUser() {
        return user;
    }

    public Food getFood() {
        return food;
    }
}
