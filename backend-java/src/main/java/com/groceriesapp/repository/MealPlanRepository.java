package com.groceriesapp.repository;

import com.groceriesapp.model.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    
    // Find meal plans by user
    List<MealPlan> findByUserId(Long userId);
    
    // Find meal plans by household
    List<MealPlan> findByHouseholdId(Long householdId);
    
    // Find meal plans for a specific date
    List<MealPlan> findByUserIdAndPlannedDate(Long userId, LocalDate plannedDate);
    
    // Find meal plans for a date range
    @Query("SELECT mp FROM MealPlan mp WHERE mp.userId = :userId AND mp.plannedDate BETWEEN :startDate AND :endDate ORDER BY mp.plannedDate, mp.mealType")
    List<MealPlan> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find upcoming meal plans
    @Query("SELECT mp FROM MealPlan mp WHERE mp.userId = :userId AND mp.plannedDate >= :today AND mp.isCompleted = false ORDER BY mp.plannedDate, mp.mealType")
    List<MealPlan> findUpcomingMealPlans(@Param("userId") Long userId, @Param("today") LocalDate today);
    
    // Find completed meal plans
    List<MealPlan> findByUserIdAndIsCompletedTrue(Long userId);
    
    // Find meal plans by recipe
    List<MealPlan> findByRecipeId(Long recipeId);
    
    // Find meal plans by meal type
    List<MealPlan> findByUserIdAndMealType(Long userId, MealPlan.MealType mealType);
    
    // Get weekly meal plan
    @Query("SELECT mp FROM MealPlan mp WHERE mp.userId = :userId AND mp.plannedDate BETWEEN :startOfWeek AND :endOfWeek ORDER BY mp.plannedDate, mp.mealType")
    List<MealPlan> getWeeklyMealPlan(@Param("userId") Long userId, @Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);
}
