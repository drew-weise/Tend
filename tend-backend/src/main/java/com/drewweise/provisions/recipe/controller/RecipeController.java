package com.drewweise.provisions.recipe.controller;

import com.drewweise.provisions.recipe.dto.RecipeSummary;
import com.drewweise.provisions.recipe.repository.RecipeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping("/count")
    public long countRecipes() {
        return recipeRepository.count();
    }

    @GetMapping
    public List<RecipeSummary> findAllRecipes() {
        return recipeRepository.findAll()
                .stream()
                .map(recipe -> new RecipeSummary(
                        recipe.getRecipeId(),
                        recipe.getRecipeTitle(),
                        recipe.getRecipeServings()
                ))
                .toList();
    }

}