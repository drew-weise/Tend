
package com.drewweise.tend.serving.repository;

import com.drewweise.tend.serving.model.Serving;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Handles persistence and retrieval of Serving records.
 */
public interface ServingRepository
        extends JpaRepository<Serving, Integer> {

    // Retrieves every serving option defined for a Food.
    List<Serving> findByFood_FoodId(Integer foodId);

    // Retrieves the preferred serving option for a Food.
    Optional<Serving> findByFood_FoodIdAndDefaultServingTrue(
            Integer foodId
    );
}
