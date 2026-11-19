
package com.drewweise.tend.recipe.controller;

import com.drewweise.tend.recipe.dto.CreateRecipeRequest;
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

    @GetMapping
    public List<SavedRecipeResponse> getSavedRecipes(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        return recipeService.getSavedRecipes(user.getUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedRecipeResponse createRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateRecipeRequest request
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        return recipeService.createRecipe(
                user.getUserId(),
                request
        );
    }

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
