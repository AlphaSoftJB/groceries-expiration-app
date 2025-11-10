package com.groceriesapp.model.nutrition;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumption_log")
public class ConsumptionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    
    private Long userId;
    
    @Column(name = "user_id", nullable = false)
    private Long itemId;
    
    @Column(name = "consumed_at", nullable = false)
    private LocalDateTime consumedAt;
    
    @Column(name = "servings_consumed", precision = 10, scale = 2)
    private BigDecimal servingsConsumed = BigDecimal.ONE;
    
    // Nutritional totals (calculated from nutrition_info * servings)
    @Column(name = "total_calories")
    private Integer totalCalories;
    
    @Column(name = "total_protein", precision = 10, scale = 2)
    private BigDecimal totalProtein;
    
    @Column(name = "total_carbs", precision = 10, scale = 2)
    private BigDecimal totalCarbs;
    
    @Column(name = "total_fat", precision = 10, scale = 2)
    private BigDecimal totalFat;
    
    @Column(name = "total_fiber", precision = 10, scale = 2)
    private BigDecimal totalFiber;
    
    @Column(name = "total_sugar", precision = 10, scale = 2)
    private BigDecimal totalSugar;
    
    @Column(name = "total_sodium", precision = 10, scale = 2)
    private BigDecimal totalSodium;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private MealType mealType;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (consumedAt == null) {
            consumedAt = LocalDateTime.now();
        }
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
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public LocalDateTime getConsumedAt() {
        return consumedAt;
    }
    
    public void setConsumedAt(LocalDateTime consumedAt) {
        this.consumedAt = consumedAt;
    }
    
    public BigDecimal getServingsConsumed() {
        return servingsConsumed;
    }
    
    public void setServingsConsumed(BigDecimal servingsConsumed) {
        this.servingsConsumed = servingsConsumed;
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
    
    public MealType getMealType() {
        return mealType;
    }
    
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Enum for Meal Type
    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK, OTHER
    }
}
