package com.drewweise.tend.recipe.dto;

import java.math.BigDecimal;
import java.util.List;

public record RecipeRequest(
        String name,
        String description,
        Integer servings,
        List<IngredientRequest> ingredients,
        List<String> instructions
) {
    public record IngredientRequest(
            Integer foodId,
            BigDecimal quantity,
            String unit
    ) {
    }
}