package com.groceriesapp.model.nutrition;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_info")
public class NutritionInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    // Serving Information
    @Column(name = "serving_size", length = 100)
    private String servingSize;
    
    @Column(name = "serving_unit", length = 50)
    private String servingUnit;
    
    @Column(name = "servings_per_container", precision = 10, scale = 2)
    private BigDecimal servingsPerContainer;
    
    // Calories
    @Column(name = "calories")
    private Integer calories;
    
    @Column(name = "calories_from_fat")
    private Integer caloriesFromFat;
    
    // Macronutrients (in grams)
    @Column(name = "total_fat", precision = 10, scale = 2)
    private BigDecimal totalFat;
    
    @Column(name = "saturated_fat", precision = 10, scale = 2)
    private BigDecimal saturatedFat;
    
    @Column(name = "trans_fat", precision = 10, scale = 2)
    private BigDecimal transFat;
    
    @Column(name = "polyunsaturated_fat", precision = 10, scale = 2)
    private BigDecimal polyunsaturatedFat;
    
    @Column(name = "monounsaturated_fat", precision = 10, scale = 2)
    private BigDecimal monounsaturatedFat;
    
    @Column(name = "cholesterol", precision = 10, scale = 2)
    private BigDecimal cholesterol; // in mg
    
    @Column(name = "sodium", precision = 10, scale = 2)
    private BigDecimal sodium; // in mg
    
    @Column(name = "total_carbohydrates", precision = 10, scale = 2)
    private BigDecimal totalCarbohydrates;
    
    @Column(name = "dietary_fiber", precision = 10, scale = 2)
    private BigDecimal dietaryFiber;
    
    @Column(name = "total_sugars", precision = 10, scale = 2)
    private BigDecimal totalSugars;
    
    @Column(name = "added_sugars", precision = 10, scale = 2)
    private BigDecimal addedSugars;
    
    @Column(name = "sugar_alcohols", precision = 10, scale = 2)
    private BigDecimal sugarAlcohols;
    
    @Column(name = "protein", precision = 10, scale = 2)
    private BigDecimal protein;
    
    // Vitamins (in mg or mcg)
    @Column(name = "vitamin_a", precision = 10, scale = 2)
    private BigDecimal vitaminA;
    
    @Column(name = "vitamin_c", precision = 10, scale = 2)
    private BigDecimal vitaminC;
    
    @Column(name = "vitamin_d", precision = 10, scale = 2)
    private BigDecimal vitaminD;
    
    @Column(name = "vitamin_e", precision = 10, scale = 2)
    private BigDecimal vitaminE;
    
    @Column(name = "vitamin_k", precision = 10, scale = 2)
    private BigDecimal vitaminK;
    
    @Column(name = "vitamin_b6", precision = 10, scale = 2)
    private BigDecimal vitaminB6;
    
    @Column(name = "vitamin_b12", precision = 10, scale = 2)
    private BigDecimal vitaminB12;
    
    // Minerals (in mg or mcg)
    @Column(name = "calcium", precision = 10, scale = 2)
    private BigDecimal calcium;
    
    @Column(name = "iron", precision = 10, scale = 2)
    private BigDecimal iron;
    
    @Column(name = "potassium", precision = 10, scale = 2)
    private BigDecimal potassium;
    
    @Column(name = "magnesium", precision = 10, scale = 2)
    private BigDecimal magnesium;
    
    @Column(name = "zinc", precision = 10, scale = 2)
    private BigDecimal zinc;
    
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
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getServingSize() {
        return servingSize;
    }
    
    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }
    
    public String getServingUnit() {
        return servingUnit;
    }
    
    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }
    
    public BigDecimal getServingsPerContainer() {
        return servingsPerContainer;
    }
    
    public void setServingsPerContainer(BigDecimal servingsPerContainer) {
        this.servingsPerContainer = servingsPerContainer;
    }
    
    public Integer getCalories() {
        return calories;
    }
    
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
    
    public Integer getCaloriesFromFat() {
        return caloriesFromFat;
    }
    
    public void setCaloriesFromFat(Integer caloriesFromFat) {
        this.caloriesFromFat = caloriesFromFat;
    }
    
    public BigDecimal getTotalFat() {
        return totalFat;
    }
    
    public void setTotalFat(BigDecimal totalFat) {
        this.totalFat = totalFat;
    }
    
    public BigDecimal getSaturatedFat() {
        return saturatedFat;
    }
    
    public void setSaturatedFat(BigDecimal saturatedFat) {
        this.saturatedFat = saturatedFat;
    }
    
    public BigDecimal getTransFat() {
        return transFat;
    }
    
    public void setTransFat(BigDecimal transFat) {
        this.transFat = transFat;
    }
    
    public BigDecimal getPolyunsaturatedFat() {
        return polyunsaturatedFat;
    }
    
    public void setPolyunsaturatedFat(BigDecimal polyunsaturatedFat) {
        this.polyunsaturatedFat = polyunsaturatedFat;
    }
    
    public BigDecimal getMonounsaturatedFat() {
        return monounsaturatedFat;
    }
    
    public void setMonounsaturatedFat(BigDecimal monounsaturatedFat) {
        this.monounsaturatedFat = monounsaturatedFat;
    }
    
    public BigDecimal getCholesterol() {
        return cholesterol;
    }
    
    public void setCholesterol(BigDecimal cholesterol) {
        this.cholesterol = cholesterol;
    }
    
    public BigDecimal getSodium() {
        return sodium;
    }
    
    public void setSodium(BigDecimal sodium) {
        this.sodium = sodium;
    }
    
    public BigDecimal getTotalCarbohydrates() {
        return totalCarbohydrates;
    }
    
    public void setTotalCarbohydrates(BigDecimal totalCarbohydrates) {
        this.totalCarbohydrates = totalCarbohydrates;
    }
    
    public BigDecimal getDietaryFiber() {
        return dietaryFiber;
    }
    
    public void setDietaryFiber(BigDecimal dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }
    
    public BigDecimal getTotalSugars() {
        return totalSugars;
    }
    
    public void setTotalSugars(BigDecimal totalSugars) {
        this.totalSugars = totalSugars;
    }
    
    public BigDecimal getAddedSugars() {
        return addedSugars;
    }
    
    public void setAddedSugars(BigDecimal addedSugars) {
        this.addedSugars = addedSugars;
    }
    
    public BigDecimal getSugarAlcohols() {
        return sugarAlcohols;
    }
    
    public void setSugarAlcohols(BigDecimal sugarAlcohols) {
        this.sugarAlcohols = sugarAlcohols;
    }
    
    public BigDecimal getProtein() {
        return protein;
    }
    
    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }
    
    public BigDecimal getVitaminA() {
        return vitaminA;
    }
    
    public void setVitaminA(BigDecimal vitaminA) {
        this.vitaminA = vitaminA;
    }
    
    public BigDecimal getVitaminC() {
        return vitaminC;
    }
    
    public void setVitaminC(BigDecimal vitaminC) {
        this.vitaminC = vitaminC;
    }
    
    public BigDecimal getVitaminD() {
        return vitaminD;
    }
    
    public void setVitaminD(BigDecimal vitaminD) {
        this.vitaminD = vitaminD;
    }
    
    public BigDecimal getVitaminE() {
        return vitaminE;
    }
    
    public void setVitaminE(BigDecimal vitaminE) {
        this.vitaminE = vitaminE;
    }
    
    public BigDecimal getVitaminK() {
        return vitaminK;
    }
    
    public void setVitaminK(BigDecimal vitaminK) {
        this.vitaminK = vitaminK;
    }
    
    public BigDecimal getVitaminB6() {
        return vitaminB6;
    }
    
    public void setVitaminB6(BigDecimal vitaminB6) {
        this.vitaminB6 = vitaminB6;
    }
    
    public BigDecimal getVitaminB12() {
        return vitaminB12;
    }
    
    public void setVitaminB12(BigDecimal vitaminB12) {
        this.vitaminB12 = vitaminB12;
    }
    
    public BigDecimal getCalcium() {
        return calcium;
    }
    
    public void setCalcium(BigDecimal calcium) {
        this.calcium = calcium;
    }
    
    public BigDecimal getIron() {
        return iron;
    }
    
    public void setIron(BigDecimal iron) {
        this.iron = iron;
    }
    
    public BigDecimal getPotassium() {
        return potassium;
    }
    
    public void setPotassium(BigDecimal potassium) {
        this.potassium = potassium;
    }
    
    public BigDecimal getMagnesium() {
        return magnesium;
    }
    
    public void setMagnesium(BigDecimal magnesium) {
        this.magnesium = magnesium;
    }
    
    public BigDecimal getZinc() {
        return zinc;
    }
    
    public void setZinc(BigDecimal zinc) {
        this.zinc = zinc;
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
