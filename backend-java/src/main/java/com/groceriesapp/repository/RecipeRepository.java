package com.groceriesapp.repository;

import com.groceriesapp.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // Find recipes by meal type
    List<Recipe> findByMealType(Recipe.MealType mealType);
    
    // Find recipes by difficulty level
    List<Recipe> findByDifficultyLevel(Recipe.DifficultyLevel difficultyLevel);
    
    // Find recipes by cuisine type
    List<Recipe> findByCuisineType(String cuisineType);
    
    // Find public recipes
    List<Recipe> findByIsPublicTrue();
    
    // Find recipes created by user
    List<Recipe> findByCreatedByUserId(Long userId);
    
    // Find recipes by tag
    @Query("SELECT r FROM Recipe r JOIN r.tags t WHERE t = :tag")
    List<Recipe> findByTag(@Param("tag") String tag);
    
    // Find recipes containing specific ingredient
    @Query("SELECT r FROM Recipe r JOIN r.ingredients i WHERE LOWER(i) LIKE LOWER(CONCAT('%', :ingredient, '%'))")
    List<Recipe> findByIngredientContaining(@Param("ingredient") String ingredient);
    
    // Find recipes by multiple ingredients (for expiring items)
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.ingredients i WHERE LOWER(i) IN :ingredients")
    List<Recipe> findByIngredientsIn(@Param("ingredients") List<String> ingredients);
    
    // Find top rated recipes
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true ORDER BY r.ratingAverage DESC, r.ratingCount DESC")
    List<Recipe> findTopRated();
    
    // Find most popular recipes
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true ORDER BY r.viewCount DESC")
    List<Recipe> findMostPopular();
    
    // Find recently added recipes
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true ORDER BY r.createdAt DESC")
    List<Recipe> findRecentlyAdded();
    
    // Search recipes by name or description
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Recipe> searchRecipes(@Param("query") String query);
    
    // Find quick recipes (under specified time)
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND (r.prepTimeMinutes + r.cookTimeMinutes) <= :maxMinutes")
    List<Recipe> findQuickRecipes(@Param("maxMinutes") Integer maxMinutes);
    
    // Find recipes within calorie range
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND r.caloriesPerServing BETWEEN :minCalories AND :maxCalories")
    List<Recipe> findByCalorieRange(@Param("minCalories") Integer minCalories, @Param("maxCalories") Integer maxCalories);
}
