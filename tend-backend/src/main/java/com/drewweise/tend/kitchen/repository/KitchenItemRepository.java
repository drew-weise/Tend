
package com.drewweise.tend.kitchen.repository;

import com.drewweise.tend.kitchen.model.KitchenItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitchenItemRepository extends JpaRepository<KitchenItem, Integer> {

    List<KitchenItem> findByUser_UserId(int userId);
}
