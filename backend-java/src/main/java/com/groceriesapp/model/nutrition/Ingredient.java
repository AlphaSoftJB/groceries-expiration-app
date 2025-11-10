package com.groceriesapp.model.nutrition;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ingredients")
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // Allergen flags
    @Column(name = "is_allergen")
    private Boolean isAllergen = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen_type")
    private AllergenType allergenType;
    
    // Dietary flags
    @Column(name = "is_vegan")
    private Boolean isVegan = true;
    
    @Column(name = "is_vegetarian")
    private Boolean isVegetarian = true;
    
    @Column(name = "is_gluten_free")
    private Boolean isGlutenFree = true;
    
    @Column(name = "is_dairy_free")
    private Boolean isDairyFree = true;
    
    @Column(name = "is_nut_free")
    private Boolean isNutFree = true;
    
    // Common names and aliases (JSON array)
    @Column(name = "aliases", columnDefinition = "JSON")
    private String aliases;
    
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsAllergen() {
        return isAllergen;
    }
    
    public void setIsAllergen(Boolean isAllergen) {
        this.isAllergen = isAllergen;
    }
    
    public AllergenType getAllergenType() {
        return allergenType;
    }
    
    public void setAllergenType(AllergenType allergenType) {
        this.allergenType = allergenType;
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
    
    public String getAliases() {
        return aliases;
    }
    
    public void setAliases(String aliases) {
        this.aliases = aliases;
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
    
    // Enum for Allergen Types
}
