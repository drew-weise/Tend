
package com.drewweise.tend.food;

import jakarta.persistence.*;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private int foodId;

    @Column(name = "name", nullable = false)
    private String name;

    public Food() {
    }

    public Food(String name) {
        this.name = name;
    }

    public int getFoodId() {
        return foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Food{" +
                "foodId=" + foodId +
                ", name='" + name + '\'' +
                '}';
    }
}
