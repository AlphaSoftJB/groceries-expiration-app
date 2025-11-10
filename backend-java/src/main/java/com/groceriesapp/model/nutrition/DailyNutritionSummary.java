package com.groceriesapp.model.nutrition;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_nutrition_summary")
public class DailyNutritionSummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    
    private Long userId;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    // Daily totals
    @Column(name = "total_calories")
    private Integer totalCalories = 0;
    
    @Column(name = "total_protein", precision = 10, scale = 2)
    private BigDecimal totalProtein = BigDecimal.ZERO;
    
    @Column(name = "total_carbs", precision = 10, scale = 2)
    private BigDecimal totalCarbs = BigDecimal.ZERO;
    
    @Column(name = "total_fat", precision = 10, scale = 2)
    private BigDecimal totalFat = BigDecimal.ZERO;
    
    @Column(name = "total_fiber", precision = 10, scale = 2)
    private BigDecimal totalFiber = BigDecimal.ZERO;
    
    @Column(name = "total_sugar", precision = 10, scale = 2)
    private BigDecimal totalSugar = BigDecimal.ZERO;
    
    @Column(name = "total_sodium", precision = 10, scale = 2)
    private BigDecimal totalSodium = BigDecimal.ZERO;
    
    // Meal breakdown
    @Column(name = "breakfast_calories")
    private Integer breakfastCalories = 0;
    
    @Column(name = "lunch_calories")
    private Integer lunchCalories = 0;
    
    @Column(name = "dinner_calories")
    private Integer dinnerCalories = 0;
    
    @Column(name = "snack_calories")
    private Integer snackCalories = 0;
    
    // Goals met
    @Column(name = "calorie_goal_met")
    private Boolean calorieGoalMet = false;
    
    @Column(name = "protein_goal_met")
    private Boolean proteinGoalMet = false;
    
    @Column(name = "carb_goal_met")
    private Boolean carbGoalMet = false;
    
    @Column(name = "fat_goal_met")
    private Boolean fatGoalMet = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Integer getTotalCalories() {
        return totalCalories;
    }
    
    public void setTotalCalories(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }
    
    public BigDecimal getTotalProtein() {
        return totalProtein;
    }
    
    public void setTotalProtein(BigDecimal totalProtein) {
        this.totalProtein = totalProtein;
    }
    
    public BigDecimal getTotalCarbs() {
        return totalCarbs;
    }
    
    public void setTotalCarbs(BigDecimal totalCarbs) {
        this.totalCarbs = totalCarbs;
    }
    
    public BigDecimal getTotalFat() {
        return totalFat;
    }
    
    public void setTotalFat(BigDecimal totalFat) {
        this.totalFat = totalFat;
    }
    
    public BigDecimal getTotalFiber() {
        return totalFiber;
    }
    
    public void setTotalFiber(BigDecimal totalFiber) {
        this.totalFiber = totalFiber;
    }
    
    public BigDecimal getTotalSugar() {
        return totalSugar;
    }
    
    public void setTotalSugar(BigDecimal totalSugar) {
        this.totalSugar = totalSugar;
    }
    
    public BigDecimal getTotalSodium() {
        return totalSodium;
    }
    
    public void setTotalSodium(BigDecimal totalSodium) {
        this.totalSodium = totalSodium;
    }
    
    public Integer getBreakfastCalories() {
        return breakfastCalories;
    }
    
    public void setBreakfastCalories(Integer breakfastCalories) {
        this.breakfastCalories = breakfastCalories;
    }
    
    public Integer getLunchCalories() {
        return lunchCalories;
    }
    
    public void setLunchCalories(Integer lunchCalories) {
        this.lunchCalories = lunchCalories;
    }
    
    public Integer getDinnerCalories() {
        return dinnerCalories;
    }
    
    public void setDinnerCalories(Integer dinnerCalories) {
        this.dinnerCalories = dinnerCalories;
    }
    
    public Integer getSnackCalories() {
        return snackCalories;
    }
    
    public void setSnackCalories(Integer snackCalories) {
        this.snackCalories = snackCalories;
    }
    
    public Boolean getCalorieGoalMet() {
        return calorieGoalMet;
    }
    
    public void setCalorieGoalMet(Boolean calorieGoalMet) {
        this.calorieGoalMet = calorieGoalMet;
    }
    
    public Boolean getProteinGoalMet() {
        return proteinGoalMet;
    }
    
    public void setProteinGoalMet(Boolean proteinGoalMet) {
        this.proteinGoalMet = proteinGoalMet;
    }
    
    public Boolean getCarbGoalMet() {
        return carbGoalMet;
    }
    
    public void setCarbGoalMet(Boolean carbGoalMet) {
        this.carbGoalMet = carbGoalMet;
    }
    
    public Boolean getFatGoalMet() {
        return fatGoalMet;
    }
    
    public void setFatGoalMet(Boolean fatGoalMet) {
        this.fatGoalMet = fatGoalMet;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
