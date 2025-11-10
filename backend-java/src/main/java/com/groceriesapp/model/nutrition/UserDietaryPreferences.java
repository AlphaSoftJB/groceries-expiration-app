package com.groceriesapp.model.nutrition;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_dietary_preferences")
public class UserDietaryPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    // Dietary restrictions
    @Column(name = "is_vegan")
    private Boolean isVegan = false;
    
    @Column(name = "is_vegetarian")
    private Boolean isVegetarian = false;
    
    @Column(name = "is_gluten_free")
    private Boolean isGlutenFree = false;
    
    @Column(name = "is_dairy_free")
    private Boolean isDairyFree = false;
    
    @Column(name = "is_nut_free")
    private Boolean isNutFree = false;
    
    @Column(name = "is_kosher")
    private Boolean isKosher = false;
    
    @Column(name = "is_halal")
    private Boolean isHalal = false;
    
    @Column(name = "is_low_carb")
    private Boolean isLowCarb = false;
    
    @Column(name = "is_keto")
    private Boolean isKeto = false;
    
    @Column(name = "is_paleo")
    private Boolean isPaleo = false;
    
    // Nutritional goals (daily targets)
    @Column(name = "daily_calorie_goal")
    private Integer dailyCalorieGoal;
    
    @Column(name = "daily_protein_goal", precision = 10, scale = 2)
    private BigDecimal dailyProteinGoal;
    
    @Column(name = "daily_carb_goal", precision = 10, scale = 2)
    private BigDecimal dailyCarbGoal;
    
    @Column(name = "daily_fat_goal", precision = 10, scale = 2)
    private BigDecimal dailyFatGoal;
    
    @Column(name = "daily_fiber_goal", precision = 10, scale = 2)
    private BigDecimal dailyFiberGoal;
    
    @Column(name = "daily_sugar_limit", precision = 10, scale = 2)
    private BigDecimal dailySugarLimit;
    
    @Column(name = "daily_sodium_limit", precision = 10, scale = 2)
    private BigDecimal dailySodiumLimit;
    
    // Preferences (JSON arrays)
    @Column(name = "avoid_ingredients", columnDefinition = "JSON")
    private String avoidIngredients; // Array of ingredient IDs to avoid
    
    @Column(name = "preferred_ingredients", columnDefinition = "JSON")
    private String preferredIngredients; // Array of ingredient IDs user prefers
    
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
    
    public Boolean getIsVegan() {
        return isVegan;
    }
    
    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }
    
    public Boolean getIsVegetarian() {
        return isVegetarian;
    }
    
    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }
    
    public Boolean getIsGlutenFree() {
        return isGlutenFree;
    }
    
    public void setIsGlutenFree(Boolean isGlutenFree) {
        this.isGlutenFree = isGlutenFree;
    }
    
    public Boolean getIsDairyFree() {
        return isDairyFree;
    }
    
    public void setIsDairyFree(Boolean isDairyFree) {
        this.isDairyFree = isDairyFree;
    }
    
    public Boolean getIsNutFree() {
        return isNutFree;
    }
    
    public void setIsNutFree(Boolean isNutFree) {
        this.isNutFree = isNutFree;
    }
    
    public Boolean getIsKosher() {
        return isKosher;
    }
    
    public void setIsKosher(Boolean isKosher) {
        this.isKosher = isKosher;
    }
    
    public Boolean getIsHalal() {
        return isHalal;
    }
    
    public void setIsHalal(Boolean isHalal) {
        this.isHalal = isHalal;
    }
    
    public Boolean getIsLowCarb() {
        return isLowCarb;
    }
    
    public void setIsLowCarb(Boolean isLowCarb) {
        this.isLowCarb = isLowCarb;
    }
    
    public Boolean getIsKeto() {
        return isKeto;
    }
    
    public void setIsKeto(Boolean isKeto) {
        this.isKeto = isKeto;
    }
    
    public Boolean getIsPaleo() {
        return isPaleo;
    }
    
    public void setIsPaleo(Boolean isPaleo) {
        this.isPaleo = isPaleo;
    }
    
    public Integer getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }
    
    public void setDailyCalorieGoal(Integer dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
    }
    
    public BigDecimal getDailyProteinGoal() {
        return dailyProteinGoal;
    }
    
    public void setDailyProteinGoal(BigDecimal dailyProteinGoal) {
        this.dailyProteinGoal = dailyProteinGoal;
    }
    
    public BigDecimal getDailyCarbGoal() {
        return dailyCarbGoal;
    }
    
    public void setDailyCarbGoal(BigDecimal dailyCarbGoal) {
        this.dailyCarbGoal = dailyCarbGoal;
    }
    
    public BigDecimal getDailyFatGoal() {
        return dailyFatGoal;
    }
    
    public void setDailyFatGoal(BigDecimal dailyFatGoal) {
        this.dailyFatGoal = dailyFatGoal;
    }
    
    public BigDecimal getDailyFiberGoal() {
        return dailyFiberGoal;
    }
    
    public void setDailyFiberGoal(BigDecimal dailyFiberGoal) {
        this.dailyFiberGoal = dailyFiberGoal;
    }
    
    public BigDecimal getDailySugarLimit() {
        return dailySugarLimit;
    }
    
    public void setDailySugarLimit(BigDecimal dailySugarLimit) {
        this.dailySugarLimit = dailySugarLimit;
    }
    
    public BigDecimal getDailySodiumLimit() {
        return dailySodiumLimit;
    }
    
    public void setDailySodiumLimit(BigDecimal dailySodiumLimit) {
        this.dailySodiumLimit = dailySodiumLimit;
    }
    
    public String getAvoidIngredients() {
        return avoidIngredients;
    }
    
    public void setAvoidIngredients(String avoidIngredients) {
        this.avoidIngredients = avoidIngredients;
    }
    
    public String getPreferredIngredients() {
        return preferredIngredients;
    }
    
    public void setPreferredIngredients(String preferredIngredients) {
        this.preferredIngredients = preferredIngredients;
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
