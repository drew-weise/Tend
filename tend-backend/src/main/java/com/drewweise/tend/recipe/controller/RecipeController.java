
package com.drewweise.tend.recipe.controller;

import com.drewweise.tend.recipe.dto.SavedRecipeResponse;
import com.drewweise.tend.recipe.service.RecipeService;
import com.drewweise.tend.user.model.User;
import com.drewweise.tend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;

    public RecipeController(
            RecipeService recipeService,
            UserService userService
    ) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    /**
     * Retrieves all recipes saved by the authenticated user.
     */
    @GetMapping
    public List<SavedRecipeResponse> getSavedRecipes(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        return recipeService.getSavedRecipes(user.getUserId());
    }

    /**
     * Saves an existing recipe to the authenticated
     * user's personal collection.
     */
    @PostMapping("/{foodId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavedRecipeResponse addSavedRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer foodId
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        return recipeService.addSavedRecipe(
                user.getUserId(),
                foodId
        );
    }

    /**
     * Removes a recipe from the authenticated
     * user's saved collection.
     *
     * Does not delete the underlying Food record.
     */
    @DeleteMapping("/{savedRecipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSavedRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer savedRecipeId
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        recipeService.removeSavedRecipe(
                user.getUserId(),
                savedRecipeId
        );
    }
}
