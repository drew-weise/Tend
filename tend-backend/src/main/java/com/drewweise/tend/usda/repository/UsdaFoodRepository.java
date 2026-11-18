
package com.drewweise.tend.usda.repository;

import com.drewweise.tend.usda.dto.UsdaFoodSearchResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Repository
public class UsdaFoodRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsdaFoodRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UsdaFoodSearchResult> search(String query) {
        String search = query == null ? "" : query.trim();

        if (search.length() < 2) {
            return List.of();
        }

        String genericSql = """
            SELECT
                usda_fdc_id AS fdc_id,
                name AS description,
                'REFERENCE' AS data_type
            FROM tend.foods
            WHERE usda_fdc_id IS NOT NULL
              AND name LIKE ?
            ORDER BY
                CASE
                    WHEN LOWER(name) = LOWER(?) THEN 0
                    WHEN LOWER(name) LIKE LOWER(?) THEN 1
                    ELSE 2
                END,
                LENGTH(name)
            LIMIT 30
            """;

        List<UsdaFoodSearchResult> generic = jdbcTemplate.query(
                genericSql,
                (rs, rowNum) -> new UsdaFoodSearchResult(
                        rs.getString("fdc_id"),
                        rs.getString("description"),
                        rs.getString("data_type")
                ),
                "%" + search + "%",
                search,
                search + "%"
        );

        String booleanSearch = "+" + String.join(
                "* +",
                search.replaceAll("[^\\p{L}\\p{N}]+", " ")
                        .trim()
                        .split("\\s+")
        ) + "*";

        String brandedSql = """
            SELECT
                fdc_id,
                description,
                data_type
            FROM usda.food
            WHERE data_type = 'branded_food'
              AND MATCH(description)
                  AGAINST (? IN BOOLEAN MODE)
            LIMIT 50
            """;

        List<UsdaFoodSearchResult> branded = jdbcTemplate.query(
                brandedSql,
                (rs, rowNum) -> new UsdaFoodSearchResult(
                        rs.getString("fdc_id"),
                        rs.getString("description"),
                        rs.getString("data_type")
                ),
                booleanSearch
        );

        List<UsdaFoodSearchResult> results = new ArrayList<>();
        results.addAll(generic);
        results.addAll(branded);

        results.sort(
                Comparator.comparingInt(
                                (UsdaFoodSearchResult food) -> score(food, search)
                        ).reversed()
                        .thenComparingInt(food -> food.description().length())
        );

        return results.stream()
                .limit(10)
                .toList();
    }

    private int score(UsdaFoodSearchResult food, String query) {
        String name = food.description().toLowerCase(Locale.ROOT);
        String search = query.toLowerCase(Locale.ROOT);
        String normalized = name.replaceAll("[^\\p{L}\\p{N}]+", " ").trim();

        int score = 0;

        if (name.equals(search)) {
            score += 100;
        } else if (normalized.equals(search)) {
            score += 100;
        } else if (name.startsWith(search)) {
            score += 60;
        } else if (normalized.startsWith(search)) {
            score += 60;
        } else if (name.contains(search)) {
            score += 30;
        }

        String[] words = search.split("\\s+");

        for (String word : words) {
            if (normalized.contains(word)) {
                score += 10;
            }
        }

        if ("REFERENCE".equals(food.dataType())) {
            score += 15;
        }

        return score;
    }
}
