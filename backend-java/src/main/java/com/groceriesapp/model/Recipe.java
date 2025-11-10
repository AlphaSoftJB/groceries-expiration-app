package com.groceriesapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;
    
    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;
    
    @Column(name = "servings")
    private Integer servings;
    
    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    
    @Column(name = "cuisine_type")
    private String cuisineType;
    
    @Column(name = "meal_type")
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    
    @ElementCollection
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "recipe_instructions", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "instruction", columnDefinition = "TEXT")
    @OrderColumn(name = "step_order")
    private List<String> instructions = new ArrayList<>();
    
    @Column(name = "calories_per_serving")
    private Integer caloriesPerServing;
    
    @Column(name = "protein_grams")
    private Double proteinGrams;
    
    @Column(name = "carbs_grams")
    private Double carbsGrams;
    
    @Column(name = "fat_grams")
    private Double fatGrams;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "source")
    private String source;
    
    @Column(name = "source_url")
    private String sourceUrl;
    
    @Column(name = "created_by_user_id")
    private Long createdByUserId;
    
    @Column(name = "is_public")
    private Boolean isPublic = true;
    
    @Column(name = "rating_average")
    private Double ratingAverage = 0.0;
    
    @Column(name = "rating_count")
    private Integer ratingCount = 0;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "save_count")
    private Integer saveCount = 0;
    
    @ElementCollection
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
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
    
    // Enums
    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
    
    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK, DESSERT, BEVERAGE
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
    
    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }
    
    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }
    
    public Integer getCookTimeMinutes() {
        return cookTimeMinutes;
    }
    
    public void setCookTimeMinutes(Integer cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }
    
    public Integer getServings() {
        return servings;
    }
    
    public void setServings(Integer servings) {
        this.servings = servings;
    }
    
    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    public String getCuisineType() {
        return cuisineType;
    }
    
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }
    
    public MealType getMealType() {
        return mealType;
    }
    
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
    
    public List<String> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    
    public List<String> getInstructions() {
        return instructions;
    }
    
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
    
    public Integer getCaloriesPerServing() {
        return caloriesPerServing;
    }
    
    public void setCaloriesPerServing(Integer caloriesPerServing) {
        this.caloriesPerServing = caloriesPerServing;
    }
    
    public Double getProteinGrams() {
        return proteinGrams;
    }
    
    public void setProteinGrams(Double proteinGrams) {
        this.proteinGrams = proteinGrams;
    }
    
    public Double getCarbsGrams() {
        return carbsGrams;
    }
    
    public void setCarbsGrams(Double carbsGrams) {
        this.carbsGrams = carbsGrams;
    }
    
    public Double getFatGrams() {
        return fatGrams;
    }
    
    public void setFatGrams(Double fatGrams) {
        this.fatGrams = fatGrams;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    public Long getCreatedByUserId() {
        return createdByUserId;
    }
    
    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Double getRatingAverage() {
        return ratingAverage;
    }
    
    public void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }
    
    public Integer getRatingCount() {
        return ratingCount;
    }
    
    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getSaveCount() {
        return saveCount;
    }
    
    public void setSaveCount(Integer saveCount) {
        this.saveCount = saveCount;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
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
