package com.drewweise.tend.usda.controller;

import com.drewweise.tend.usda.dto.UsdaFoodSearchResult;
import com.drewweise.tend.usda.repository.UsdaFoodRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class UsdaFoodController {

    private final UsdaFoodRepository repository;

    public UsdaFoodController(UsdaFoodRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/search")
    public List<UsdaFoodSearchResult> search(
            @RequestParam String q) {

        return repository.search(q);
    }
}