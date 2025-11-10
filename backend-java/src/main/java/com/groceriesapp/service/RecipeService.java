package com.groceriesapp.service;

import com.groceriesapp.model.Item;
import com.groceriesapp.model.Recipe;
import com.groceriesapp.repository.ItemRepository;
import com.groceriesapp.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    /**
     * Get recipe suggestions based on expiring items in household
     * Smart algorithm that prioritizes recipes using items expiring soon
     */
    @Transactional(readOnly = true)
    public List<RecipeSuggestion> suggestRecipesForExpiringItems(Long householdId, Integer daysAhead) {
        // Get items expiring within specified days
        LocalDate cutoffDate = LocalDate.now().plusDays(daysAhead != null ? daysAhead : 7);
        List<Item> expiringItems = itemRepository.findByHouseholdIdAndExpirationDateBefore(householdId, cutoffDate);
        
        if (expiringItems.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Extract item names (ingredients)
        List<String> availableIngredients = expiringItems.stream()
                .map(item -> item.getName().toLowerCase())
                .collect(Collectors.toList());
        
        // Find recipes that use these ingredients
        List<Recipe> matchingRecipes = recipeRepository.findByIngredientsIn(availableIngredients);
        
        // Score and rank recipes
        List<RecipeSuggestion> suggestions = new ArrayList<>();
        for (Recipe recipe : matchingRecipes) {
            RecipeSuggestion suggestion = scoreRecipe(recipe, expiringItems, availableIngredients);
            suggestions.add(suggestion);
        }
        
        // Sort by score (highest first)
        suggestions.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        return suggestions;
    }
    
    /**
     * Score recipe based on multiple factors
     */
    private RecipeSuggestion scoreRecipe(Recipe recipe, List<Item> expiringItems, List<String> availableIngredients) {
        double score = 0.0;
        List<String> matchedIngredients = new ArrayList<>();
        List<String> missingIngredients = new ArrayList<>();
        int urgentItemsUsed = 0;
        
        // Check which ingredients match
        for (String recipeIngredient : recipe.getIngredients()) {
            String normalizedIngredient = recipeIngredient.toLowerCase();
            boolean matched = false;
            
            for (String available : availableIngredients) {
                if (normalizedIngredient.contains(available) || available.contains(normalizedIngredient)) {
                    matchedIngredients.add(recipeIngredient);
                    matched = true;
                    
                    // Check if this is an urgent item (expiring within 3 days)
                    for (Item item : expiringItems) {
                        if (item.getName().equalsIgnoreCase(available) && 
                            item.getExpirationDate() != null &&
                            item.getExpirationDate().isBefore(LocalDate.now().plusDays(3))) {
                            urgentItemsUsed++;
                            score += 15.0; // Bonus for using urgent items
                        }
                    }
                    break;
                }
            }
            
            if (!matched) {
                missingIngredients.add(recipeIngredient);
            }
        }
        
        // Calculate match percentage
        double matchPercentage = (double) matchedIngredients.size() / recipe.getIngredients().size() * 100;
        score += matchPercentage; // Base score from match percentage
        
        // Bonus for high match percentage
        if (matchPercentage >= 80) {
            score += 20.0;
        } else if (matchPercentage >= 60) {
            score += 10.0;
        }
        
        // Bonus for recipe rating
        if (recipe.getRatingAverage() != null) {
            score += recipe.getRatingAverage() * 5; // Up to 25 points for 5-star recipe
        }
        
        // Bonus for popularity
        if (recipe.getViewCount() != null && recipe.getViewCount() > 100) {
            score += Math.min(recipe.getViewCount() / 100.0, 10.0); // Up to 10 points
        }
        
        // Penalty for too many missing ingredients
        if (missingIngredients.size() > 5) {
            score -= (missingIngredients.size() - 5) * 2.0;
        }
        
        // Bonus for quick recipes
        if (recipe.getPrepTimeMinutes() != null && recipe.getCookTimeMinutes() != null) {
            int totalTime = recipe.getPrepTimeMinutes() + recipe.getCookTimeMinutes();
            if (totalTime <= 30) {
                score += 10.0;
            } else if (totalTime <= 60) {
                score += 5.0;
            }
        }
        
        // Create suggestion object
        RecipeSuggestion suggestion = new RecipeSuggestion();
        suggestion.setRecipe(recipe);
        suggestion.setScore(Math.max(0, score)); // Ensure non-negative
        suggestion.setMatchedIngredients(matchedIngredients);
        suggestion.setMissingIngredients(missingIngredients);
        suggestion.setMatchPercentage(matchPercentage);
        suggestion.setUrgentItemsUsed(urgentItemsUsed);
        
        return suggestion;
    }
    
    /**
     * Get all recipes
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findByIsPublicTrue();
    }
    
    /**
     * Get recipe by ID
     */
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElse(null);
    }
    
    /**
     * Search recipes
     */
    public List<Recipe> searchRecipes(String query) {
        return recipeRepository.searchRecipes(query);
    }
    
    /**
     * Get recipes by meal type
     */
    public List<Recipe> getRecipesByMealType(Recipe.MealType mealType) {
        return recipeRepository.findByMealType(mealType);
    }
    
    /**
     * Get top rated recipes
     */
    public List<Recipe> getTopRatedRecipes() {
        return recipeRepository.findTopRated();
    }
    
    /**
     * Get quick recipes (under 30 minutes)
     */
    public List<Recipe> getQuickRecipes() {
        return recipeRepository.findQuickRecipes(30);
    }
    
    /**
     * Create new recipe
     */
    @Transactional
    public Recipe createRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    /**
     * Update recipe
     */
    @Transactional
    public Recipe updateRecipe(Long id, Recipe updatedRecipe) {
        Recipe existing = recipeRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        
        // Update fields
        existing.setName(updatedRecipe.getName());
        existing.setDescription(updatedRecipe.getDescription());
        existing.setPrepTimeMinutes(updatedRecipe.getPrepTimeMinutes());
        existing.setCookTimeMinutes(updatedRecipe.getCookTimeMinutes());
        existing.setServings(updatedRecipe.getServings());
        existing.setDifficultyLevel(updatedRecipe.getDifficultyLevel());
        existing.setCuisineType(updatedRecipe.getCuisineType());
        existing.setMealType(updatedRecipe.getMealType());
        existing.setIngredients(updatedRecipe.getIngredients());
        existing.setInstructions(updatedRecipe.getInstructions());
        existing.setImageUrl(updatedRecipe.getImageUrl());
        existing.setTags(updatedRecipe.getTags());
        
        return recipeRepository.save(existing);
    }
    
    /**
     * Delete recipe
     */
    @Transactional
    public boolean deleteRecipe(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Increment view count
     */
    @Transactional
    public void incrementViewCount(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe != null) {
            recipe.setViewCount(recipe.getViewCount() + 1);
            recipeRepository.save(recipe);
        }
    }
    
    /**
     * Rate recipe
     */
    @Transactional
    public void rateRecipe(Long recipeId, Double rating) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe != null && rating >= 1.0 && rating <= 5.0) {
            // Calculate new average
            double currentTotal = recipe.getRatingAverage() * recipe.getRatingCount();
            int newCount = recipe.getRatingCount() + 1;
            double newAverage = (currentTotal + rating) / newCount;
            
            recipe.setRatingAverage(newAverage);
            recipe.setRatingCount(newCount);
            recipeRepository.save(recipe);
        }
    }
    
    /**
     * Recipe Suggestion DTO
     */
    public static class RecipeSuggestion {
        private Recipe recipe;
        private double score;
        private List<String> matchedIngredients;
        private List<String> missingIngredients;
        private double matchPercentage;
        private int urgentItemsUsed;
        
        // Getters and Setters
        public Recipe getRecipe() {
            return recipe;
        }
        
        public void setRecipe(Recipe recipe) {
            this.recipe = recipe;
        }
        
        public double getScore() {
            return score;
        }
        
        public void setScore(double score) {
            this.score = score;
        }
        
        public List<String> getMatchedIngredients() {
            return matchedIngredients;
        }
        
        public void setMatchedIngredients(List<String> matchedIngredients) {
            this.matchedIngredients = matchedIngredients;
        }
        
        public List<String> getMissingIngredients() {
            return missingIngredients;
        }
        
        public void setMissingIngredients(List<String> missingIngredients) {
            this.missingIngredients = missingIngredients;
        }
        
        public double getMatchPercentage() {
            return matchPercentage;
        }
        
        public void setMatchPercentage(double matchPercentage) {
            this.matchPercentage = matchPercentage;
        }
        
        public int getUrgentItemsUsed() {
            return urgentItemsUsed;
        }
        
        public void setUrgentItemsUsed(int urgentItemsUsed) {
            this.urgentItemsUsed = urgentItemsUsed;
        }
    }
}
