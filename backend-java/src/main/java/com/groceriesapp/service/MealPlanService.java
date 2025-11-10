package com.groceriesapp.service;

import com.groceriesapp.model.MealPlan;
import com.groceriesapp.model.Recipe;
import com.groceriesapp.repository.MealPlanRepository;
import com.groceriesapp.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MealPlanService {
    
    @Autowired
    private MealPlanRepository mealPlanRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    /**
     * Get meal plans for a user
     */
    public List<MealPlan> getMealPlansByUser(Long userId) {
        return mealPlanRepository.findByUserId(userId);
    }
    
    /**
     * Get meal plans for a specific date
     */
    public List<MealPlan> getMealPlansForDate(Long userId, LocalDate date) {
        return mealPlanRepository.findByUserIdAndPlannedDate(userId, date);
    }
    
    /**
     * Get meal plans for a date range
     */
    public List<MealPlan> getMealPlansForDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealPlanRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }
    
    /**
     * Get weekly meal plan (current week)
     */
    public WeeklyMealPlan getWeeklyMealPlan(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        List<MealPlan> mealPlans = mealPlanRepository.getWeeklyMealPlan(userId, startOfWeek, endOfWeek);
        
        return buildWeeklyMealPlan(mealPlans, startOfWeek, endOfWeek);
    }
    
    /**
     * Get weekly meal plan for specific week
     */
    public WeeklyMealPlan getWeeklyMealPlanForWeek(Long userId, LocalDate weekStart) {
        LocalDate startOfWeek = weekStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        
        List<MealPlan> mealPlans = mealPlanRepository.getWeeklyMealPlan(userId, startOfWeek, endOfWeek);
        
        return buildWeeklyMealPlan(mealPlans, startOfWeek, endOfWeek);
    }
    
    /**
     * Build weekly meal plan with nutrition summary
     */
    private WeeklyMealPlan buildWeeklyMealPlan(List<MealPlan> mealPlans, LocalDate startDate, LocalDate endDate) {
        WeeklyMealPlan weeklyPlan = new WeeklyMealPlan();
        weeklyPlan.setStartDate(startDate);
        weeklyPlan.setEndDate(endDate);
        weeklyPlan.setMealPlans(mealPlans);
        
        // Calculate nutrition totals
        int totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        
        for (MealPlan mealPlan : mealPlans) {
            if (mealPlan.getRecipeId() != null) {
                Recipe recipe = recipeRepository.findById(mealPlan.getRecipeId()).orElse(null);
                if (recipe != null) {
                    int servings = mealPlan.getServings() != null ? mealPlan.getServings() : 1;
                    
                    if (recipe.getCaloriesPerServing() != null) {
                        totalCalories += recipe.getCaloriesPerServing() * servings;
                    }
                    if (recipe.getProteinGrams() != null) {
                        totalProtein += recipe.getProteinGrams() * servings;
                    }
                    if (recipe.getCarbsGrams() != null) {
                        totalCarbs += recipe.getCarbsGrams() * servings;
                    }
                    if (recipe.getFatGrams() != null) {
                        totalFat += recipe.getFatGrams() * servings;
                    }
                }
            }
        }
        
        weeklyPlan.setTotalCalories(totalCalories);
        weeklyPlan.setTotalProtein(totalProtein);
        weeklyPlan.setTotalCarbs(totalCarbs);
        weeklyPlan.setTotalFat(totalFat);
        
        // Calculate daily averages
        int daysCount = 7;
        weeklyPlan.setAvgDailyCalories(totalCalories / daysCount);
        weeklyPlan.setAvgDailyProtein(totalProtein / daysCount);
        weeklyPlan.setAvgDailyCarbs(totalCarbs / daysCount);
        weeklyPlan.setAvgDailyFat(totalFat / daysCount);
        
        return weeklyPlan;
    }
    
    /**
     * Create meal plan
     */
    @Transactional
    public MealPlan createMealPlan(MealPlan mealPlan) {
        return mealPlanRepository.save(mealPlan);
    }
    
    /**
     * Update meal plan
     */
    @Transactional
    public MealPlan updateMealPlan(Long id, MealPlan updatedMealPlan) {
        MealPlan existing = mealPlanRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        
        existing.setRecipeId(updatedMealPlan.getRecipeId());
        existing.setPlannedDate(updatedMealPlan.getPlannedDate());
        existing.setMealType(updatedMealPlan.getMealType());
        existing.setServings(updatedMealPlan.getServings());
        existing.setNotes(updatedMealPlan.getNotes());
        
        return mealPlanRepository.save(existing);
    }
    
    /**
     * Mark meal plan as completed
     */
    @Transactional
    public MealPlan completeMealPlan(Long id) {
        MealPlan mealPlan = mealPlanRepository.findById(id).orElse(null);
        if (mealPlan != null) {
            mealPlan.setIsCompleted(true);
            mealPlan.setCompletedAt(LocalDateTime.now());
            return mealPlanRepository.save(mealPlan);
        }
        return null;
    }
    
    /**
     * Delete meal plan
     */
    @Transactional
    public boolean deleteMealPlan(Long id) {
        if (mealPlanRepository.existsById(id)) {
            mealPlanRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Get upcoming meal plans
     */
    public List<MealPlan> getUpcomingMealPlans(Long userId) {
        return mealPlanRepository.findUpcomingMealPlans(userId, LocalDate.now());
    }
    
    /**
     * Auto-generate meal plan for the week based on expiring items
     */
    @Transactional
    public List<MealPlan> generateWeeklyMealPlan(Long userId, Long householdId) {
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusDays(6);
        
        // Clear existing meal plans for this week
        List<MealPlan> existingPlans = mealPlanRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        mealPlanRepository.deleteAll(existingPlans);
        
        // Get recipe suggestions (this would ideally use RecipeService)
        List<Recipe> availableRecipes = recipeRepository.findByIsPublicTrue();
        if (availableRecipes.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<MealPlan> generatedPlans = new ArrayList<>();
        Random random = new Random();
        
        // Generate meal plans for each day
        for (int day = 0; day < 7; day++) {
            LocalDate currentDate = startDate.plusDays(day);
            
            // Breakfast
            if (!availableRecipes.isEmpty()) {
                Recipe breakfastRecipe = availableRecipes.get(random.nextInt(availableRecipes.size()));
                MealPlan breakfast = new MealPlan();
                breakfast.setUserId(userId);
                breakfast.setHouseholdId(householdId);
                breakfast.setRecipeId(breakfastRecipe.getId());
                breakfast.setPlannedDate(currentDate);
                breakfast.setMealType(MealPlan.MealType.BREAKFAST);
                breakfast.setServings(1);
                generatedPlans.add(mealPlanRepository.save(breakfast));
            }
            
            // Lunch
            if (!availableRecipes.isEmpty()) {
                Recipe lunchRecipe = availableRecipes.get(random.nextInt(availableRecipes.size()));
                MealPlan lunch = new MealPlan();
                lunch.setUserId(userId);
                lunch.setHouseholdId(householdId);
                lunch.setRecipeId(lunchRecipe.getId());
                lunch.setPlannedDate(currentDate);
                lunch.setMealType(MealPlan.MealType.LUNCH);
                lunch.setServings(1);
                generatedPlans.add(mealPlanRepository.save(lunch));
            }
            
            // Dinner
            if (!availableRecipes.isEmpty()) {
                Recipe dinnerRecipe = availableRecipes.get(random.nextInt(availableRecipes.size()));
                MealPlan dinner = new MealPlan();
                dinner.setUserId(userId);
                dinner.setHouseholdId(householdId);
                dinner.setRecipeId(dinnerRecipe.getId());
                dinner.setPlannedDate(currentDate);
                dinner.setMealType(MealPlan.MealType.DINNER);
                dinner.setServings(1);
                generatedPlans.add(mealPlanRepository.save(dinner));
            }
        }
        
        return generatedPlans;
    }
    
    /**
     * Weekly Meal Plan DTO
     */
    public static class WeeklyMealPlan {
        private LocalDate startDate;
        private LocalDate endDate;
        private List<MealPlan> mealPlans;
        private int totalCalories;
        private double totalProtein;
        private double totalCarbs;
        private double totalFat;
        private int avgDailyCalories;
        private double avgDailyProtein;
        private double avgDailyCarbs;
        private double avgDailyFat;
        
        // Getters and Setters
        public LocalDate getStartDate() {
            return startDate;
        }
        
        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }
        
        public LocalDate getEndDate() {
            return endDate;
        }
        
        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }
        
        public List<MealPlan> getMealPlans() {
            return mealPlans;
        }
        
        public void setMealPlans(List<MealPlan> mealPlans) {
            this.mealPlans = mealPlans;
        }
        
        public int getTotalCalories() {
            return totalCalories;
        }
        
        public void setTotalCalories(int totalCalories) {
            this.totalCalories = totalCalories;
        }
        
        public double getTotalProtein() {
            return totalProtein;
        }
        
        public void setTotalProtein(double totalProtein) {
            this.totalProtein = totalProtein;
        }
        
        public double getTotalCarbs() {
            return totalCarbs;
        }
        
        public void setTotalCarbs(double totalCarbs) {
            this.totalCarbs = totalCarbs;
        }
        
        public double getTotalFat() {
            return totalFat;
        }
        
        public void setTotalFat(double totalFat) {
            this.totalFat = totalFat;
        }
        
        public int getAvgDailyCalories() {
            return avgDailyCalories;
        }
        
        public void setAvgDailyCalories(int avgDailyCalories) {
            this.avgDailyCalories = avgDailyCalories;
        }
        
        public double getAvgDailyProtein() {
            return avgDailyProtein;
        }
        
        public void setAvgDailyProtein(double avgDailyProtein) {
            this.avgDailyProtein = avgDailyProtein;
        }
        
        public double getAvgDailyCarbs() {
            return avgDailyCarbs;
        }
        
        public void setAvgDailyCarbs(double avgDailyCarbs) {
            this.avgDailyCarbs = avgDailyCarbs;
        }
        
        public double getAvgDailyFat() {
            return avgDailyFat;
        }
        
        public void setAvgDailyFat(double avgDailyFat) {
            this.avgDailyFat = avgDailyFat;
        }
    }
}
