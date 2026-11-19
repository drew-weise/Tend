
package com.drewweise.tend.recipe.service;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.food.repository.FoodRepository;
import com.drewweise.tend.recipe.dto.SavedRecipeResponse;
import com.drewweise.tend.recipe.model.SavedRecipe;
import com.drewweise.tend.recipe.repository.SavedRecipeRepository;
import com.drewweise.tend.user.model.User;
import com.drewweise.tend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecipeService {

    private final SavedRecipeRepository savedRecipeRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public RecipeService(
            SavedRecipeRepository savedRecipeRepository,
            FoodRepository foodRepository,
            UserRepository userRepository
    ) {
        this.savedRecipeRepository = savedRecipeRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all recipes saved by a user.
     */
    @Transactional(readOnly = true)
    public List<SavedRecipeResponse> getSavedRecipes(Integer userId) {
        return savedRecipeRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Saves an existing recipe to a user's collection.
     *
     * Only shared recipes or recipes owned by the user
     * may be saved.
     */
    @Transactional
    public SavedRecipeResponse addSavedRecipe(
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
                        "Recipe not found"
                ));

        // Prevent users from saving another user's private recipe.
        if (food.getUser() != null &&
                !food.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Recipe not found"
            );
        }

        // Prevent duplicate saved recipes.
        if (savedRecipeRepository
                .existsByUser_UserIdAndFood_FoodId(userId, foodId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Recipe is already saved"
            );
        }

        SavedRecipe savedRecipe = new SavedRecipe(user, food);

        return toResponse(savedRecipeRepository.save(savedRecipe));
    }

    /**
     * Removes a recipe from a user's saved collection.
     *
     * The underlying Food record is not deleted.
     * Ownership is enforced through the repository query.
     */
    @Transactional
    public void removeSavedRecipe(
            Integer userId,
            Integer savedRecipeId
    ) {
        SavedRecipe savedRecipe = savedRecipeRepository
                .findBySavedRecipeIdAndUser_UserId(
                        savedRecipeId,
                        userId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Saved recipe not found"
                ));

        savedRecipeRepository.delete(savedRecipe);
    }

    /**
     * Converts a SavedRecipe entity into a response DTO.
     */
    private SavedRecipeResponse toResponse(SavedRecipe savedRecipe) {
        return new SavedRecipeResponse(
                savedRecipe.getSavedRecipeId(),
                savedRecipe.getFood().getFoodId(),
                savedRecipe.getFood().getName()
        );
    }
}
