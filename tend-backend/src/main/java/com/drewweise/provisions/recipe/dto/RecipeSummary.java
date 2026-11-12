package com.drewweise.provisions.recipe.dto;

public record RecipeSummary(
        int recipeId,
        String recipeTitle,
        int recipeServings
) {}