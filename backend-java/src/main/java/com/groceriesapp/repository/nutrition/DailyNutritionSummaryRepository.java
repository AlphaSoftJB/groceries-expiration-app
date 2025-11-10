package com.groceriesapp.repository.nutrition;

import com.groceriesapp.model.nutrition.DailyNutritionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for DailyNutritionSummary entities
 */
@Repository
public interface DailyNutritionSummaryRepository extends JpaRepository<DailyNutritionSummary, Long> {
    
    /**
     * Find summary for a user on a specific date
     */
    Optional<DailyNutritionSummary> findByUserIdAndDate(Long userId, LocalDate date);
    
    /**
     * Find summaries for a user in a date range
     */
    List<DailyNutritionSummary> findByUserIdAndDateBetweenOrderByDateDesc(
        Long userId,
        LocalDate startDate,
        LocalDate endDate
    );
    
    /**
     * Find recent summaries for a user
     */
    List<DailyNutritionSummary> findTop30ByUserIdOrderByDateDesc(Long userId);
    
    /**
     * Alias for findByUserIdAndDateBetweenOrderByDateDesc
     */
    default List<DailyNutritionSummary> findByUserIdAndDateBetween(
        Long userId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
    }
}
