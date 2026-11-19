
package com.drewweise.tend.recipe.dto;

public record SavedRecipeResponse(
        int id,
        int foodId,
        String name
) {
}
