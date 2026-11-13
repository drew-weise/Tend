
package com.drewweise.tend.kitchen.service;

import com.drewweise.tend.kitchen.dto.KitchenItemResponse;
import com.drewweise.tend.kitchen.model.KitchenItem;
import com.drewweise.tend.kitchen.repository.KitchenItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KitchenService {

    private final KitchenItemRepository kitchenItemRepository;

    public KitchenService(KitchenItemRepository kitchenItemRepository) {
        this.kitchenItemRepository = kitchenItemRepository;
    }

    @Transactional(readOnly = true)
    public List<KitchenItemResponse> getKitchenItems(int userId) {
        return kitchenItemRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private KitchenItemResponse toResponse(KitchenItem item) {
        return new KitchenItemResponse(
                item.getKitchenItemId(),
                item.getFood().getFoodId(),
                item.getFood().getName()
        );
    }
}
