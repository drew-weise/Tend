
package com.drewweise.tend.kitchen.service;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.food.repository.FoodRepository;
import com.drewweise.tend.kitchen.dto.KitchenItemResponse;
import com.drewweise.tend.kitchen.model.KitchenItem;
import com.drewweise.tend.kitchen.repository.KitchenItemRepository;
import com.drewweise.tend.user.model.User;
import com.drewweise.tend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class KitchenService {

    private final KitchenItemRepository kitchenItemRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public KitchenService(
            KitchenItemRepository kitchenItemRepository,
            FoodRepository foodRepository,
            UserRepository userRepository
    ) {
        this.kitchenItemRepository = kitchenItemRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all Foods saved to a user's Kitchen.
     */
    @Transactional(readOnly = true)
    public List<KitchenItemResponse> getKitchenItems(Integer userId) {
        return kitchenItemRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Saves an existing Food to a user's Kitchen.
     *
     * Only shared Foods or Foods owned by the user
     * may be saved.
     */
    @Transactional
    public KitchenItemResponse addKitchenItem(
            Integer userId,
            Integer foodId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Food not found"
                ));

        // A user cannot save another user's private Food.
        if (food.getUser() != null &&
                food.getUser().getUserId() != userId) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Food not found"
            );
        }

        // Prevent duplicate entries.
        if (kitchenItemRepository
                .existsByUser_UserIdAndFood_FoodId(userId, foodId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Food is already saved to Kitchen"
            );
        }

        KitchenItem item = new KitchenItem(user, food);

        return toResponse(kitchenItemRepository.save(item));
    }

    /**
     * Removes a Food from a user's Kitchen.
     *
     * The ownership check is part of the database query,
     * so users cannot delete another user's KitchenItems.
     */
    @Transactional
    public void removeKitchenItem(
            Integer userId,
            Integer kitchenItemId
    ) {
        KitchenItem item = kitchenItemRepository
                .findByKitchenItemIdAndUser_UserId(
                        kitchenItemId,
                        userId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kitchen item not found"
                ));

        kitchenItemRepository.delete(item);
    }

    /**
     * Converts a KitchenItem entity into the DTO
     * returned to the frontend.
     */
    private KitchenItemResponse toResponse(KitchenItem item) {
        return new KitchenItemResponse(
                item.getKitchenItemId(),
                item.getFood().getFoodId(),
                item.getFood().getName()
        );
    }
}
