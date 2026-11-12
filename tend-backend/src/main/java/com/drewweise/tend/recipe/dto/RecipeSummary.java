package com.drewweise.tend.recipe.dto;

public record RecipeSummary(
        int recipeId,
        String recipeTitle,
        int recipeServings
) {}