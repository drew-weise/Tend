package com.drewweise.tend.usda.dto;

public record UsdaFoodSearchResult(
        String fdcId,
        String description,
        String dataType
) {}