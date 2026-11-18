package com.drewweise.tend.usda.repository;

import com.drewweise.tend.usda.dto.UsdaFoodSearchResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsdaFoodRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsdaFoodRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UsdaFoodSearchResult> search(String query) {
        String sql = """
            SELECT fdc_id, description, data_type
            FROM usda.food
            WHERE description LIKE ?
              AND data_type IN (
                  'foundation_food',
                  'sr_legacy_food',
                  'survey_fndds_food',
                  'branded_food'
              )
            LIMIT 20
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new UsdaFoodSearchResult(
                        rs.getString("fdc_id"),
                        rs.getString("description"),
                        rs.getString("data_type")
                ),
                "%" + query + "%"
        );
    }
}