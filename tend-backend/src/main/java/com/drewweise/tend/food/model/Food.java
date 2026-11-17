
package com.drewweise.tend.food.model;

import com.drewweise.tend.user.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Represents anything edible in Tend.
 *
 * A Food can be a basic ingredient (banana, egg),
 * a packaged product (frozen pizza), or a complete
 * homemade meal (chicken Alfredo).
 *
 * Related information such as nutrition, serving sizes,
 * ingredients, and preparation instructions are stored
 * in separate entities.
 */
@Entity
@Table(name = "foods")
public class Food {

    // Primary key. MySQL generates the ID automatically.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Integer foodId;

    // Basic identifying information about the food.
    @Column(name = "name", nullable = false)
    private String name;

    // Optional description. TEXT allows longer descriptions.
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * The user who created this food.
     *
     * Many Foods can belong to one User (@ManyToOne).
     * This field can be null for shared/system foods.
     *
     * LAZY means Hibernate does not automatically load
     * the User until the relationship is accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Optional reference to the Food this was copied from.
     *
     * Example:
     * A user copies a shared chicken Alfredo recipe
     * and customizes it. Their new Food can reference
     * the original Food here.
     *
     * This is a self-referencing relationship because
     * both records belong to the foods table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_food_id")
    private Food sourceFood;

    /**
     * Number of portions produced by the entire food.
     *
     * Example:
     * Banana = 1
     * Homemade lasagna = 8
     *
     * This is different from Serving, which describes
     * the size or measurement of an individual portion.
     *
     * BigDecimal avoids floating-point precision issues.
     * Defaults to one serving.
     */
    @Column(name = "yield_servings", nullable = false)
    private BigDecimal yieldServings = BigDecimal.ONE;

    /**
     * Required by JPA/Hibernate.
     *
     * Hibernate uses this constructor when loading
     * Food records from the database.
     */
    protected Food() {
    }

    /**
     * Constructor used when creating a new Food.
     *
     * The database generates foodId when the Food
     * is saved, so we don't provide an ID here.
     *
     * User can be null for shared/system foods.
     */
    public Food(String name, User user) {
        this.name = name;
        this.user = user;
    }

    // GETTERS: Allow other classes to read Food properties.

    public Integer getFoodId() {
        return foodId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }

    public Food getSourceFood() {
        return sourceFood;
    }

    public BigDecimal getYieldServings() {
        return yieldServings;
    }

    // SETTERS: Allow selected properties to be modified.

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSourceFood(Food sourceFood) {
        this.sourceFood = sourceFood;
    }

    /**
     * Updates the number of servings this Food produces.
     *
     * Rejects null, zero, and negative values because
     * a Food must produce at least some positive quantity.
     *
     * compareTo() is used instead of > because
     * BigDecimal is an object, not a primitive number.
     */
    public void setYieldServings(BigDecimal yieldServings) {
        if (yieldServings == null ||
                yieldServings.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Yield servings must be greater than zero"
            );
        }

        this.yieldServings = yieldServings;
    }
}
