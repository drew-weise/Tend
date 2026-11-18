
package com.drewweise.tend.kitchen.model;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Represents a Food saved to a User's Kitchen.
 *
 * Multiple users can save the same Food without
 * duplicating its information.
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
@Getter
public class KitchenItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kitchen_item_id")
    private Integer kitchenItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    // Required by JPA/Hibernate.
    protected KitchenItem() {
    }

    public KitchenItem(User user, Food food) {
        if (user == null || food == null) {
            throw new IllegalArgumentException(
                    "User and Food cannot be null"
            );
        }

        this.user = user;
        this.food = food;
    }
}
