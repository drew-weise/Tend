package com.drewweise.tend.recipe.repository;

import com.drewweise.tend.recipe.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

}