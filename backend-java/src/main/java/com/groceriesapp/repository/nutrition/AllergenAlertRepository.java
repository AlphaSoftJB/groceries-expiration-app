package com.groceriesapp.repository.nutrition;

import com.groceriesapp.model.nutrition.AllergenAlert;
import com.groceriesapp.model.nutrition.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for AllergenAlert entities
 */
@Repository
public interface AllergenAlertRepository extends JpaRepository<AllergenAlert, Long> {
    
    /**
     * Find all alerts for a user
     */
    List<AllergenAlert> findByUserId(Long userId);
    
    /**
     * Find pending alerts for a user
     */
    List<AllergenAlert> findByUserIdAndStatus(Long userId, AlertStatus status);
    
    /**
     * Find alerts for an item
     */
    List<AllergenAlert> findByItemId(Long itemId);
    
    /**
     * Find all alerts for a user ordered by creation date descending
     */
    List<AllergenAlert> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find recent alerts for a user (last 10)
     */
    @org.springframework.data.jpa.repository.Query("SELECT a FROM AllergenAlert a WHERE a.userId = ?1 ORDER BY a.createdAt DESC LIMIT 10")
    List<AllergenAlert> findRecentAlerts(Long userId);
    
    /**
     * Find severe alerts for a user (severity = SEVERE or LIFE_THREATENING)
     */
    @org.springframework.data.jpa.repository.Query("SELECT a FROM AllergenAlert a WHERE a.userId = ?1 AND (a.severity = 'SEVERE' OR a.severity = 'LIFE_THREATENING') ORDER BY a.createdAt DESC")
    List<AllergenAlert> findSevereAlerts(Long userId);
}
