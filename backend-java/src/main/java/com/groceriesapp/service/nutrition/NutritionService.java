package com.groceriesapp.service.nutrition;

import com.groceriesapp.model.*;
import com.groceriesapp.model.nutrition.*;
import com.groceriesapp.repository.*;
import com.groceriesapp.repository.nutrition.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NutritionService {
    
    @Autowired
    private NutritionInfoRepository nutritionInfoRepository;
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Autowired
    private UserAllergenRepository userAllergenRepository;
    
    @Autowired
    private UserDietaryPreferencesRepository userDietaryPreferencesRepository;
    
    @Autowired
    private AllergenAlertRepository allergenAlertRepository;
    
    @Autowired
    private ConsumptionLogRepository consumptionLogRepository;
    
    @Autowired
    private DailyNutritionSummaryRepository dailyNutritionSummaryRepository;
    
    /**
     * Parse nutrition label text from OCR and create NutritionInfo
     */
    public NutritionInfo parseNutritionLabel(String ocrText) {
        NutritionInfo nutritionInfo = new NutritionInfo();
        
        // Parse serving size
        String servingSize = extractValue(ocrText, "Serving Size", ":");
        if (servingSize != null) {
            nutritionInfo.setServingSize(servingSize);
        }
        
        // Parse servings per container
        String servingsPerContainer = extractNumericValue(ocrText, "Servings Per Container");
        if (servingsPerContainer != null) {
            nutritionInfo.setServingsPerContainer(new BigDecimal(servingsPerContainer));
        }
        
        // Parse calories
        String calories = extractNumericValue(ocrText, "Calories");
        if (calories != null) {
            nutritionInfo.setCalories(Integer.parseInt(calories));
        }
        
        // Parse macronutrients
        nutritionInfo.setTotalFat(parseNutrient(ocrText, "Total Fat"));
        nutritionInfo.setSaturatedFat(parseNutrient(ocrText, "Saturated Fat"));
        nutritionInfo.setTransFat(parseNutrient(ocrText, "Trans Fat"));
        nutritionInfo.setCholesterol(parseNutrient(ocrText, "Cholesterol"));
        nutritionInfo.setSodium(parseNutrient(ocrText, "Sodium"));
        nutritionInfo.setTotalCarbohydrates(parseNutrient(ocrText, "Total Carbohydrate"));
        nutritionInfo.setDietaryFiber(parseNutrient(ocrText, "Dietary Fiber"));
        nutritionInfo.setTotalSugars(parseNutrient(ocrText, "Total Sugars"));
        nutritionInfo.setAddedSugars(parseNutrient(ocrText, "Added Sugars"));
        nutritionInfo.setProtein(parseNutrient(ocrText, "Protein"));
        
        // Parse vitamins and minerals
        nutritionInfo.setVitaminD(parseNutrient(ocrText, "Vitamin D"));
        nutritionInfo.setCalcium(parseNutrient(ocrText, "Calcium"));
        nutritionInfo.setIron(parseNutrient(ocrText, "Iron"));
        nutritionInfo.setPotassium(parseNutrient(ocrText, "Potassium"));
        
        return nutritionInfo;
    }
    
    /**
     * Parse ingredients list from OCR text
     */
    public List<Ingredient> parseIngredientsList(String ocrText) {
        List<Ingredient> ingredients = new ArrayList<>();
        
        // Find ingredients section
        String ingredientsText = extractIngredientsSection(ocrText);
        if (ingredientsText == null || ingredientsText.isEmpty()) {
            return ingredients;
        }
        
        // Split by common delimiters
        String[] parts = ingredientsText.split("[,;]");
        
        for (String part : parts) {
            String ingredientName = part.trim().toLowerCase();
            if (ingredientName.isEmpty()) {
                continue;
            }
            
            // Try to find existing ingredient
            Optional<Ingredient> ingredientOpt = ingredientRepository.findByNameIgnoreCase(ingredientName);
            Ingredient ingredient = ingredientOpt.orElse(null);
            
            if (ingredient == null) {
                // Check if it matches any aliases
                ingredient = findIngredientByAlias(ingredientName);
            }
            
            if (ingredient == null) {
                // Create new ingredient
                ingredient = new Ingredient();
                ingredient.setName(ingredientName);
                ingredient.setIsAllergen(false);
                ingredient = ingredientRepository.save(ingredient);
            }
            
            ingredients.add(ingredient);
        }
        
        return ingredients;
    }
    
    /**
     * Check for allergens in an item for a specific user
     */
    @Transactional
    public List<AllergenAlert> checkForAllergens(Long userId, Long itemId, List<Long> ingredientIds) {
        List<AllergenAlert> alerts = new ArrayList<>();
        
        // Get user's allergens
        List<UserAllergen> userAllergens = userAllergenRepository.findByUserId(userId);
        
        if (userAllergens.isEmpty()) {
            return alerts;
        }
        
        // Fetch ingredients
        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);
        
        // Check each ingredient
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getIsAllergen()) {
                // Check if user is allergic to this
                for (UserAllergen userAllergen : userAllergens) {
                    if (ingredient.getAllergenType() == userAllergen.getAllergenType()) {
                        // Create alert
                        AllergenAlert alert = new AllergenAlert();
                        alert.setUserId(userId);
                        alert.setItemId(itemId);
                        alert.setAllergenType(userAllergen.getAllergenType());
                        alert.setAllergenName(ingredient.getName());
                        alert.setSeverity(userAllergen.getSeverity());
                        alert.setStatus(AlertStatus.PENDING);
                        
                        alert = allergenAlertRepository.save(alert);
                        alerts.add(alert);
                    }
                }
            }
        }
        
        return alerts;
    }
    
    /**
     * Check if item meets dietary restrictions
     */
    public Map<String, Boolean> checkDietaryRestrictions(Long userId, List<Long> ingredientIds) {
        Map<String, Boolean> violations = new HashMap<>();
        
        Optional<UserDietaryPreferences> prefsOpt = userDietaryPreferencesRepository.findByUserId(userId);
        if (!prefsOpt.isPresent()) {
            return violations;
        }
        
        UserDietaryPreferences prefs = prefsOpt.get();
        
        // Fetch ingredients
        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);
        
        // Check vegan
        if (prefs.getIsVegan()) {
            boolean isVegan = ingredients.stream().allMatch(Ingredient::getIsVegan);
            violations.put("vegan", !isVegan);
        }
        
        // Check vegetarian
        if (prefs.getIsVegetarian()) {
            boolean isVegetarian = ingredients.stream().allMatch(Ingredient::getIsVegetarian);
            violations.put("vegetarian", !isVegetarian);
        }
        
        // Check gluten-free
        if (prefs.getIsGlutenFree()) {
            boolean isGlutenFree = ingredients.stream().allMatch(Ingredient::getIsGlutenFree);
            violations.put("gluten_free", !isGlutenFree);
        }
        
        // Check dairy-free
        if (prefs.getIsDairyFree()) {
            boolean isDairyFree = ingredients.stream().allMatch(Ingredient::getIsDairyFree);
            violations.put("dairy_free", !isDairyFree);
        }
        
        // Check nut-free
        if (prefs.getIsNutFree()) {
            boolean isNutFree = ingredients.stream().allMatch(Ingredient::getIsNutFree);
            violations.put("nut_free", !isNutFree);
        }
        
        return violations;
    }
    
    /**
     * Log consumption of an item
     */
    @Transactional
    public ConsumptionLog logConsumption(Long userId, Long itemId, BigDecimal servings, ConsumptionLog.MealType mealType) {
        ConsumptionLog log = new ConsumptionLog();
        log.setUserId(userId);
        log.setItemId(itemId);
        log.setServingsConsumed(servings);
        log.setMealType(mealType);
        log.setConsumedAt(LocalDateTime.now());
        
        // Get nutrition info for item
        Optional<NutritionInfo> nutritionInfoOpt = nutritionInfoRepository.findByItemId(itemId);
        NutritionInfo nutritionInfo = nutritionInfoOpt.orElse(null);
        
        if (nutritionInfo != null) {
            // Calculate totals based on servings
            if (nutritionInfo.getCalories() != null) {
                log.setTotalCalories((int) (nutritionInfo.getCalories() * servings.doubleValue()));
            }
            
            if (nutritionInfo.getProtein() != null) {
                log.setTotalProtein(nutritionInfo.getProtein().multiply(servings));
            }
            
            if (nutritionInfo.getTotalCarbohydrates() != null) {
                log.setTotalCarbs(nutritionInfo.getTotalCarbohydrates().multiply(servings));
            }
            
            if (nutritionInfo.getTotalFat() != null) {
                log.setTotalFat(nutritionInfo.getTotalFat().multiply(servings));
            }
            
            if (nutritionInfo.getDietaryFiber() != null) {
                log.setTotalFiber(nutritionInfo.getDietaryFiber().multiply(servings));
            }
            
            if (nutritionInfo.getTotalSugars() != null) {
                log.setTotalSugar(nutritionInfo.getTotalSugars().multiply(servings));
            }
            
            if (nutritionInfo.getSodium() != null) {
                log.setTotalSodium(nutritionInfo.getSodium().multiply(servings));
            }
        }
        
        log = consumptionLogRepository.save(log);
        
        // Update daily summary
        updateDailySummary(userId, log);
        
        return log;
    }
    
    /**
     * Get daily nutrition summary for a user
     */
    public DailyNutritionSummary getDailySummary(Long userId, LocalDate date) {
        Optional<DailyNutritionSummary> summaryOpt = dailyNutritionSummaryRepository.findByUserIdAndDate(userId, date);
        return summaryOpt.orElse(null);
    }
    
    /**
     * Get nutrition insights for a user over a date range
     */
    public Map<String, Object> getNutritionInsights(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> insights = new HashMap<>();
        
        List<DailyNutritionSummary> summaries = dailyNutritionSummaryRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);  
        if (summaries.isEmpty()) {
            return insights;
        }
        
        // Calculate averages
        double avgCalories = summaries.stream()
                .mapToInt(DailyNutritionSummary::getTotalCalories)
                .average()
                .orElse(0);
        
        double avgProtein = summaries.stream()
                .map(DailyNutritionSummary::getTotalProtein)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0);
        
        double avgCarbs = summaries.stream()
                .map(DailyNutritionSummary::getTotalCarbs)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0);
        
        double avgFat = summaries.stream()
                .map(DailyNutritionSummary::getTotalFat)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0);
        
        insights.put("avgCalories", avgCalories);
        insights.put("avgProtein", avgProtein);
        insights.put("avgCarbs", avgCarbs);
        insights.put("avgFat", avgFat);
        
        // Calculate goal achievement rate
        Optional<UserDietaryPreferences> prefsOpt = userDietaryPreferencesRepository.findByUserId(userId);
        if (prefsOpt.isPresent() && prefsOpt.get().getDailyCalorieGoal() != null) {
            UserDietaryPreferences prefs = prefsOpt.get();
            long daysMetGoal = summaries.stream()
                    .filter(DailyNutritionSummary::getCalorieGoalMet)
                    .count();
            
            double goalAchievementRate = (double) daysMetGoal / summaries.size() * 100;
            insights.put("goalAchievementRate", goalAchievementRate);
        }
        
        return insights;
    }
    
    // Helper methods
    
    private void updateDailySummary(Long userId, ConsumptionLog log) {
        LocalDate date = log.getConsumedAt().toLocalDate();
        
        Optional<DailyNutritionSummary> summaryOpt = dailyNutritionSummaryRepository
                .findByUserIdAndDate(userId, date);
        
        DailyNutritionSummary summary;
        if (!summaryOpt.isPresent()) {
            summary = new DailyNutritionSummary();
            summary.setUserId(userId);
            summary.setDate(date);
            summary.setTotalCalories(0);
            summary.setTotalProtein(BigDecimal.ZERO);
            summary.setTotalCarbs(BigDecimal.ZERO);
            summary.setTotalFat(BigDecimal.ZERO);
            summary.setTotalFiber(BigDecimal.ZERO);
            summary.setTotalSugar(BigDecimal.ZERO);
            summary.setTotalSodium(BigDecimal.ZERO);
        } else {
            summary = summaryOpt.get();
        }
        
        // Add consumption to totals
        if (log.getTotalCalories() != null) {
            summary.setTotalCalories(summary.getTotalCalories() + log.getTotalCalories());
        }
        
        if (log.getTotalProtein() != null) {
            summary.setTotalProtein(summary.getTotalProtein().add(log.getTotalProtein()));
        }
        
        if (log.getTotalCarbs() != null) {
            summary.setTotalCarbs(summary.getTotalCarbs().add(log.getTotalCarbs()));
        }
        
        if (log.getTotalFat() != null) {
            summary.setTotalFat(summary.getTotalFat().add(log.getTotalFat()));
        }
        
        if (log.getTotalFiber() != null) {
            summary.setTotalFiber(summary.getTotalFiber().add(log.getTotalFiber()));
        }
        
        if (log.getTotalSugar() != null) {
            summary.setTotalSugar(summary.getTotalSugar().add(log.getTotalSugar()));
        }
        
        if (log.getTotalSodium() != null) {
            summary.setTotalSodium(summary.getTotalSodium().add(log.getTotalSodium()));
        }
        
        // Update meal breakdown
        if (log.getMealType() != null && log.getTotalCalories() != null) {
            ConsumptionLog.MealType mealType = log.getMealType();
            if (ConsumptionLog.MealType.BREAKFAST.equals(mealType)) {
                summary.setBreakfastCalories(
                    (summary.getBreakfastCalories() != null ? summary.getBreakfastCalories() : 0) + 
                    log.getTotalCalories()
                );
            } else if (ConsumptionLog.MealType.LUNCH.equals(mealType)) {
                summary.setLunchCalories(
                    (summary.getLunchCalories() != null ? summary.getLunchCalories() : 0) + 
                    log.getTotalCalories()
                );
            } else if (ConsumptionLog.MealType.DINNER.equals(mealType)) {
                summary.setDinnerCalories(
                    (summary.getDinnerCalories() != null ? summary.getDinnerCalories() : 0) + 
                    log.getTotalCalories()
                );
            } else if (ConsumptionLog.MealType.SNACK.equals(mealType)) {
                summary.setSnackCalories(
                    (summary.getSnackCalories() != null ? summary.getSnackCalories() : 0) + 
                    log.getTotalCalories()
                );
            }
        }
        
        // Check if goals are met
        Optional<UserDietaryPreferences> prefsOpt = userDietaryPreferencesRepository.findByUserId(userId);
        if (prefsOpt.isPresent()) {
            UserDietaryPreferences prefs = prefsOpt.get();
            if (prefs.getDailyCalorieGoal() != null) {
                summary.setCalorieGoalMet(
                    summary.getTotalCalories() >= prefs.getDailyCalorieGoal() * 0.9 &&
                    summary.getTotalCalories() <= prefs.getDailyCalorieGoal() * 1.1
                );
            }
            
            if (prefs.getDailyProteinGoal() != null) {
                summary.setProteinGoalMet(
                    summary.getTotalProtein().compareTo(prefs.getDailyProteinGoal()) >= 0
                );
            }
        }
        
        dailyNutritionSummaryRepository.save(summary);
    }
    
    private String extractValue(String text, String label, String delimiter) {
        int startIndex = text.indexOf(label);
        if (startIndex == -1) {
            return null;
        }
        
        startIndex = text.indexOf(delimiter, startIndex);
        if (startIndex == -1) {
            return null;
        }
        
        int endIndex = text.indexOf("\n", startIndex);
        if (endIndex == -1) {
            endIndex = text.length();
        }
        
        return text.substring(startIndex + delimiter.length(), endIndex).trim();
    }
    
    private String extractNumericValue(String text, String label) {
        String value = extractValue(text, label, ":");
        if (value == null) {
            return null;
        }
        
        // Extract only numbers
        return value.replaceAll("[^0-9.]", "");
    }
    
    private BigDecimal parseNutrient(String text, String nutrientName) {
        String value = extractNumericValue(text, nutrientName);
        if (value != null && !value.isEmpty()) {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private String extractIngredientsSection(String text) {
        int startIndex = text.toLowerCase().indexOf("ingredients:");
        if (startIndex == -1) {
            return null;
        }
        
        // Find end of ingredients section (usually at next section or end of text)
        int endIndex = text.indexOf("\n\n", startIndex);
        if (endIndex == -1) {
            endIndex = text.length();
        }
        
        return text.substring(startIndex + "ingredients:".length(), endIndex).trim();
    }
    
    private Ingredient findIngredientByAlias(String name) {
        List<Ingredient> allIngredients = ingredientRepository.findAll();
        
        for (Ingredient ingredient : allIngredients) {
            if (ingredient.getAliases() != null && ingredient.getAliases().contains(name)) {
                return ingredient;
            }
        }
        
        return null;
    }
}
