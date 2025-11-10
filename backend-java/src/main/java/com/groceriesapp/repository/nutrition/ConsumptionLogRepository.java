package com.groceriesapp.repository.nutrition;

import com.groceriesapp.model.nutrition.ConsumptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ConsumptionLog entities
 */
@Repository
public interface ConsumptionLogRepository extends JpaRepository<ConsumptionLog, Long> {
    
    /**
     * Find consumption logs for a user
     */
    List<ConsumptionLog> findByUserIdOrderByConsumedAtDesc(Long userId);
    
    /**
     * Find consumption logs for a user in a date range
     */
    @Query("SELECT c FROM ConsumptionLog c WHERE c.userId = :userId AND c.consumedAt BETWEEN :startDate AND :endDate ORDER BY c.consumedAt DESC")
    List<ConsumptionLog> findByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find consumption logs by meal type
     */
    List<ConsumptionLog> findByUserIdAndMealType(Long userId, String mealType);
    
    /**
     * Find recent consumption logs for a user (last 20)
     */
    @Query("SELECT c FROM ConsumptionLog c WHERE c.userId = ?1 ORDER BY c.consumedAt DESC LIMIT 20")
    List<ConsumptionLog> findRecentLogs(Long userId);
    
    /**
     * Find consumption logs for current week
     */
    @Query("SELECT c FROM ConsumptionLog c WHERE c.userId = ?1 AND c.consumedAt >= ?2 ORDER BY c.consumedAt DESC")
    List<ConsumptionLog> findCurrentWeek(Long userId, LocalDateTime weekStart);
    
    /**
     * Find consumption logs for current month
     */
    @Query("SELECT c FROM ConsumptionLog c WHERE c.userId = ?1 AND c.consumedAt >= ?2 ORDER BY c.consumedAt DESC")
    List<ConsumptionLog> findCurrentMonth(Long userId, LocalDateTime monthStart);
}
