package com.groceriesapp.repository.nutrition;

import com.groceriesapp.model.nutrition.NutritionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for NutritionInfo entities
 */
@Repository
public interface NutritionInfoRepository extends JpaRepository<NutritionInfo, Long> {
    
    /**
     * Find nutrition info by item ID
     */
    Optional<NutritionInfo> findByItemId(Long itemId);
    
    /**
     * Find all items with nutrition info for a user
     */
    @Query("SELECT n FROM NutritionInfo n WHERE n.itemId IN (SELECT i.id FROM Item i WHERE i.userId = :userId)")
    List<NutritionInfo> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find items with calories in a range
     */
    @Query("SELECT n FROM NutritionInfo n WHERE n.calories BETWEEN :minCalories AND :maxCalories")
    List<NutritionInfo> findByCaloriesRange(
        @Param("minCalories") Double minCalories,
        @Param("maxCalories") Double maxCalories
    );
    
    /**
     * Find high protein items (> threshold)
     */
    @Query("SELECT n FROM NutritionInfo n WHERE n.protein > :threshold")
    List<NutritionInfo> findHighProteinItems(@Param("threshold") Double threshold);
}
