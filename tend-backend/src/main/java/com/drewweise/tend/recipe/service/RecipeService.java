
package com.drewweise.tend.recipe.service;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.usda.repository.UsdaFoodRepository;
import com.drewweise.tend.recipe.model.RecipeFood;
import com.drewweise.tend.recipe.repository.RecipeFoodRepository;
import com.drewweise.tend.food.repository.FoodRepository;
import com.drewweise.tend.preparation.model.FoodPreparation;
import com.drewweise.tend.preparation.repository.FoodPreparationRepository;
import com.drewweise.tend.recipe.dto.RecipeRequest;
import com.drewweise.tend.recipe.dto.RecipeResponse;
import com.drewweise.tend.recipe.model.Recipe;
import com.drewweise.tend.recipe.repository.RecipeRepository;
import com.drewweise.tend.user.model.User;
import com.drewweise.tend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository savedRecipeRepository;
    private final FoodRepository foodRepository;
    private final RecipeFoodRepository recipeFoodRepository;
    private final FoodPreparationRepository foodPreparationRepository;
    private final UserRepository userRepository;
    private final UsdaFoodRepository usdaFoodRepository;

    public RecipeService(
            RecipeRepository savedRecipeRepository,
            FoodRepository foodRepository,
            RecipeFoodRepository recipeFoodRepository,
            FoodPreparationRepository foodPreparationRepository,
            UserRepository userRepository,
            UsdaFoodRepository usdaFoodRepository
    ) {
        this.savedRecipeRepository = savedRecipeRepository;
        this.foodRepository = foodRepository;
        this.recipeFoodRepository = recipeFoodRepository;
        this.foodPreparationRepository = foodPreparationRepository;
        this.userRepository = userRepository;
        this.usdaFoodRepository = usdaFoodRepository;
    }

    @Transactional(readOnly = true)
    public List<RecipeResponse> getSavedRecipes(Integer userId) {
        return savedRecipeRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RecipeResponse createRecipe(
            Integer userId,
            RecipeRequest request
    ) {
        if (request == null) {
            throw badRequest("Recipe data is required");
        }

        if (request.name() == null || request.name().isBlank()) {
            throw badRequest("Recipe name is required");
        }

        if (request.servings() == null || request.servings() < 1) {
            throw badRequest("Servings must be at least 1");
        }

        if (request.ingredients() == null ||
                request.ingredients().isEmpty()) {
            throw badRequest("At least one ingredient is required");
        }

        for (RecipeRequest.IngredientRequest ingredient
                : request.ingredients()) {

            if (ingredient == null ||
                    ingredient.usdaFdcId() == null || ingredient.usdaFdcId() <= 0) {
                throw badRequest("Every ingredient needs a positive usdaFdcId");
            }

            if (ingredient.quantity() == null ||
                    ingredient.quantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw badRequest("Ingredient quantity must be positive");
            }

            if (ingredient.unit() == null ||
                    ingredient.unit().isBlank()) {
                throw badRequest("Every ingredient needs a unit");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // Verify USDA references before writing anything to Tend. No ingredient Foods are imported.
        for (Long fdcId : request.ingredients().stream()
                .map(RecipeRequest.IngredientRequest::usdaFdcId).distinct().toList()) {
            if (usdaFoodRepository.findByFdcId(fdcId).isEmpty()) {
                throw badRequest("USDA food was not found: " + fdcId);
            }
        }

        // Create the recipe itself.
        Food recipe = new Food(request.name().trim(), user);
        recipe.setDescription(request.description());
        recipe.setYieldServings(
                BigDecimal.valueOf(request.servings())
        );

        recipe = foodRepository.save(recipe);

        // Store USDA references, quantities, and units for this recipe.
        for (RecipeRequest.IngredientRequest ingredient : request.ingredients()) {

            RecipeFood relationship = new RecipeFood(
                    recipe,
                    ingredient.usdaFdcId(),
                    ingredient.quantity(),
                    ingredient.unit().trim()
            );

            recipeFoodRepository.save(relationship);
        }

        // Save optional preparation instructions.
        if (request.instructions() != null) {
            String instructions = request.instructions()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(step -> !step.isEmpty())
                    .collect(Collectors.joining("\n"));

            if (!instructions.isBlank()) {
                FoodPreparation preparation =
                        new FoodPreparation(recipe);

                preparation.setInstructions(instructions);

                foodPreparationRepository.save(preparation);
            }
        }

        // Automatically add the new recipe to the user's collection.
        Recipe savedRecipe = new Recipe(user, recipe);

        return toResponse(
                savedRecipeRepository.save(savedRecipe)
        );
    }

    @Transactional
    public RecipeResponse addSavedRecipe(
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

        if (food.getUser() != null &&
                !Objects.equals(
                        food.getUser().getUserId(),
                        userId
                )) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Recipe not found"
            );
        }

        if (savedRecipeRepository
                .existsByUser_UserIdAndFood_FoodId(userId, foodId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Recipe is already saved"
            );
        }

        Recipe savedRecipe = new Recipe(user, food);

        return toResponse(
                savedRecipeRepository.save(savedRecipe)
        );
    }

    @Transactional
    public void removeSavedRecipe(
            Integer userId,
            Integer savedRecipeId
    ) {
        Recipe savedRecipe = savedRecipeRepository
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

    private RecipeResponse toResponse(Recipe savedRecipe) {
        return new RecipeResponse(
                savedRecipe.getSavedRecipeId(),
                savedRecipe.getFood().getFoodId(),
                savedRecipe.getFood().getName()
        );
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}
