package com.drewweise.tend.usda.repository;

import com.drewweise.tend.usda.dto.UsdaFoodSearchResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class UsdaFoodRepository {
    private final JdbcTemplate usdaJdbcTemplate;

    public UsdaFoodRepository(@Qualifier("usdaJdbcTemplate") JdbcTemplate usdaJdbcTemplate) {
        this.usdaJdbcTemplate = usdaJdbcTemplate;
    }

    public Optional<UsdaFoodSearchResult> findByFdcId(Long fdcId) {
        return usdaJdbcTemplate.query(
                "SELECT fdc_id, description, data_type FROM food WHERE fdc_id = ? LIMIT 1",
                (rs, rowNum) -> new UsdaFoodSearchResult(rs.getString("fdc_id"),
                        rs.getString("description"), rs.getString("data_type")),
                fdcId.toString()).stream().findFirst();
    }

    public List<UsdaFoodSearchResult> search(String query) {
        String search = query == null ? "" : query.trim();
        String words = search.replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
        if (search.length() < 2 || words.isEmpty()) return List.of();
        String booleanSearch = "+" + String.join("* +", words.split("\\s+")) + "*";

        // Both generic and branded foods come directly from USDA's full-text index.
        String sql = """
                SELECT fdc_id, description, data_type
                FROM food
                WHERE MATCH(description) AGAINST (? IN BOOLEAN MODE)
                ORDER BY CASE WHEN data_type = 'branded_food' THEN 1 ELSE 0 END,
                         MATCH(description) AGAINST (? IN BOOLEAN MODE) DESC,
                         CHAR_LENGTH(description), fdc_id
                LIMIT 20
                """;
        return usdaJdbcTemplate.query(sql,
                (rs, rowNum) -> new UsdaFoodSearchResult(rs.getString("fdc_id"),
                        rs.getString("description"), rs.getString("data_type")),
                booleanSearch, booleanSearch);
    }
}
