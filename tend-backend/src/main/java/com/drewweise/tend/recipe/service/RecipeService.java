
package com.drewweise.tend.recipe.service;

import com.drewweise.tend.food.model.Food;
import com.drewweise.tend.food.model.FoodIngredient;
import com.drewweise.tend.food.repository.FoodIngredientRepository;
import com.drewweise.tend.food.repository.FoodRepository;
import com.drewweise.tend.preparation.model.FoodPreparation;
import com.drewweise.tend.preparation.repository.FoodPreparationRepository;
import com.drewweise.tend.recipe.dto.CreateRecipeRequest;
import com.drewweise.tend.recipe.dto.SavedRecipeResponse;
import com.drewweise.tend.recipe.model.SavedRecipe;
import com.drewweise.tend.recipe.repository.SavedRecipeRepository;
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

    private final SavedRecipeRepository savedRecipeRepository;
    private final FoodRepository foodRepository;
    private final FoodIngredientRepository foodIngredientRepository;
    private final FoodPreparationRepository foodPreparationRepository;
    private final UserRepository userRepository;

    public RecipeService(
            SavedRecipeRepository savedRecipeRepository,
            FoodRepository foodRepository,
            FoodIngredientRepository foodIngredientRepository,
            FoodPreparationRepository foodPreparationRepository,
            UserRepository userRepository
    ) {
        this.savedRecipeRepository = savedRecipeRepository;
        this.foodRepository = foodRepository;
        this.foodIngredientRepository = foodIngredientRepository;
        this.foodPreparationRepository = foodPreparationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<SavedRecipeResponse> getSavedRecipes(Integer userId) {
        return savedRecipeRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SavedRecipeResponse createRecipe(
            Integer userId,
            CreateRecipeRequest request
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

        for (CreateRecipeRequest.IngredientRequest ingredient
                : request.ingredients()) {

            if (ingredient == null ||
                    ingredient.foodId() == null ||
                    ingredient.foodId() <= 0) {
                throw badRequest("Every ingredient needs an existing foodId");
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

        // Resolve every ingredient before writing anything. Never create ingredient Foods.
        List<Food> ingredientFoods = request.ingredients().stream()
                .map(ingredient -> foodRepository.findById(ingredient.foodId())
                        .filter(food -> food.getUser() == null ||
                                Objects.equals(food.getUser().getUserId(), userId))
                        .orElseThrow(() -> badRequest(
                                "Ingredient foodId is invalid or unavailable: " + ingredient.foodId())))
                .toList();

        // Create the recipe itself.
        Food recipe = new Food(request.name().trim(), user);
        recipe.setDescription(request.description());
        recipe.setYieldServings(
                BigDecimal.valueOf(request.servings())
        );

        recipe = foodRepository.save(recipe);

        // Connect the recipe to the selected existing Foods.
        for (int index = 0; index < request.ingredients().size(); index++) {
            CreateRecipeRequest.IngredientRequest ingredient = request.ingredients().get(index);
            Food ingredientFood = ingredientFoods.get(index);

            FoodIngredient relationship = new FoodIngredient(
                    recipe,
                    ingredientFood,
                    ingredient.quantity(),
                    ingredient.unit().trim()
            );

            foodIngredientRepository.save(relationship);
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
        SavedRecipe savedRecipe = new SavedRecipe(user, recipe);

        return toResponse(
                savedRecipeRepository.save(savedRecipe)
        );
    }

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

        SavedRecipe savedRecipe = new SavedRecipe(user, food);

        return toResponse(
                savedRecipeRepository.save(savedRecipe)
        );
    }

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

    private SavedRecipeResponse toResponse(SavedRecipe savedRecipe) {
        return new SavedRecipeResponse(
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
