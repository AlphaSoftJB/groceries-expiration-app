package com.groceriesapp.controller;

import com.groceriesapp.model.Recipe;
import com.groceriesapp.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class RecipeGraphQLController {
    
    @Autowired
    private RecipeService recipeService;
    
    @QueryMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }
    
    @QueryMapping
    public Recipe getRecipeById(@Argument Long id) {
        return recipeService.getRecipeById(id);
    }
    
    @QueryMapping
    public List<Recipe> searchRecipes(@Argument String query) {
        return recipeService.searchRecipes(query);
    }
    
    @QueryMapping
    public List<Recipe> getRecipesByMealType(@Argument Recipe.MealType mealType) {
        return recipeService.getRecipesByMealType(mealType);
    }
    
    @QueryMapping
    public List<Recipe> getTopRatedRecipes() {
        return recipeService.getTopRatedRecipes();
    }
    
    @QueryMapping
    public List<Recipe> getQuickRecipes() {
        return recipeService.getQuickRecipes();
    }
    
    @QueryMapping
    public List<RecipeService.RecipeSuggestion> suggestRecipesForExpiringItems(
            @Argument Long householdId,
            @Argument Integer daysAhead) {
        return recipeService.suggestRecipesForExpiringItems(householdId, daysAhead);
    }
    
    @MutationMapping
    public Recipe createRecipe(@Argument Map<String, Object> input) {
        Recipe recipe = mapInputToRecipe(input);
        return recipeService.createRecipe(recipe);
    }
    
    @MutationMapping
    public Recipe updateRecipe(@Argument Long id, @Argument Map<String, Object> input) {
        Recipe recipe = mapInputToRecipe(input);
        return recipeService.updateRecipe(id, recipe);
    }
    
    @MutationMapping
    public Boolean deleteRecipe(@Argument Long id) {
        return recipeService.deleteRecipe(id);
    }
    
    @MutationMapping
    public Recipe rateRecipe(@Argument Long recipeId, @Argument Double rating) {
        recipeService.rateRecipe(recipeId, rating);
        return recipeService.getRecipeById(recipeId);
    }
    
    @MutationMapping
    public Recipe viewRecipe(@Argument Long recipeId) {
        recipeService.incrementViewCount(recipeId);
        return recipeService.getRecipeById(recipeId);
    }
    
    /**
     * Helper method to map input to Recipe object
     */
    @SuppressWarnings("unchecked")
    private Recipe mapInputToRecipe(Map<String, Object> input) {
        Recipe recipe = new Recipe();
        
        if (input.containsKey("name")) {
            recipe.setName((String) input.get("name"));
        }
        if (input.containsKey("description")) {
            recipe.setDescription((String) input.get("description"));
        }
        if (input.containsKey("prepTimeMinutes")) {
            recipe.setPrepTimeMinutes((Integer) input.get("prepTimeMinutes"));
        }
        if (input.containsKey("cookTimeMinutes")) {
            recipe.setCookTimeMinutes((Integer) input.get("cookTimeMinutes"));
        }
        if (input.containsKey("servings")) {
            recipe.setServings((Integer) input.get("servings"));
        }
        if (input.containsKey("difficultyLevel")) {
            recipe.setDifficultyLevel(Recipe.DifficultyLevel.valueOf((String) input.get("difficultyLevel")));
        }
        if (input.containsKey("cuisineType")) {
            recipe.setCuisineType((String) input.get("cuisineType"));
        }
        if (input.containsKey("mealType")) {
            recipe.setMealType(Recipe.MealType.valueOf((String) input.get("mealType")));
        }
        if (input.containsKey("ingredients")) {
            recipe.setIngredients((List<String>) input.get("ingredients"));
        }
        if (input.containsKey("instructions")) {
            recipe.setInstructions((List<String>) input.get("instructions"));
        }
        if (input.containsKey("caloriesPerServing")) {
            recipe.setCaloriesPerServing((Integer) input.get("caloriesPerServing"));
        }
        if (input.containsKey("proteinGrams")) {
            recipe.setProteinGrams(((Number) input.get("proteinGrams")).doubleValue());
        }
        if (input.containsKey("carbsGrams")) {
            recipe.setCarbsGrams(((Number) input.get("carbsGrams")).doubleValue());
        }
        if (input.containsKey("fatGrams")) {
            recipe.setFatGrams(((Number) input.get("fatGrams")).doubleValue());
        }
        if (input.containsKey("imageUrl")) {
            recipe.setImageUrl((String) input.get("imageUrl"));
        }
        if (input.containsKey("videoUrl")) {
            recipe.setVideoUrl((String) input.get("videoUrl"));
        }
        if (input.containsKey("source")) {
            recipe.setSource((String) input.get("source"));
        }
        if (input.containsKey("sourceUrl")) {
            recipe.setSourceUrl((String) input.get("sourceUrl"));
        }
        if (input.containsKey("createdByUserId")) {
            recipe.setCreatedByUserId(Long.valueOf(input.get("createdByUserId").toString()));
        }
        if (input.containsKey("isPublic")) {
            recipe.setIsPublic((Boolean) input.get("isPublic"));
        }
        if (input.containsKey("tags")) {
            recipe.setTags((List<String>) input.get("tags"));
        }
        
        return recipe;
    }
}
