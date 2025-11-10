package com.groceriesapp.controller.nutrition;

import com.groceriesapp.model.*;
import com.groceriesapp.model.nutrition.*;
import com.groceriesapp.repository.*;
import com.groceriesapp.repository.nutrition.*;
import com.groceriesapp.service.nutrition.EnhancedOCRService;
import com.groceriesapp.service.nutrition.NutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GraphQL Controller for Nutrition & Allergen Tracking
 */
@Controller
public class NutritionGraphQLController {
    
    @Autowired
    private NutritionInfoRepository nutritionInfoRepository;
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Autowired
    private ItemIngredientRepository itemIngredientRepository;
    
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
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private NutritionService nutritionService;
    
    @Autowired
    private EnhancedOCRService enhancedOCRService;
    
    // ========================================================================
    // QUERIES - Nutrition Info
    // ========================================================================
    
    @QueryMapping
    public NutritionInfo getItemNutrition(@Argument Long itemId) {
        return nutritionInfoRepository.findByItemId(itemId).orElse(null);
    }
    
    @QueryMapping
    public List<NutritionInfo> getNutritionByUser(@AuthenticationPrincipal User user) {
        return nutritionInfoRepository.findByUserId(user.getId());
    }
    
    // ========================================================================
    // QUERIES - Ingredients
    // ========================================================================
    
    @QueryMapping
    public List<ItemIngredient> getItemIngredients(@Argument Long itemId) {
        return itemIngredientRepository.findByItemIdOrderByPositionAsc(itemId);
    }
    
    @QueryMapping
    public List<Ingredient> searchIngredients(@Argument String query) {
        return ingredientRepository.searchByName(query);
    }
    
    @QueryMapping
    public List<Ingredient> getAllergenIngredients() {
        return ingredientRepository.findByIsAllergenTrue();
    }
    
    @QueryMapping
    public Ingredient getIngredientById(@Argument Long id) {
        return ingredientRepository.findById(id).orElse(null);
    }
    
    // ========================================================================
    // QUERIES - User Allergens
    // ========================================================================
    
    @QueryMapping
    public List<UserAllergen> getUserAllergens(@AuthenticationPrincipal User user) {
        return userAllergenRepository.findByUserId(user.getId());
    }
    
    @QueryMapping
    public List<UserAllergen> getSevereAllergens(@AuthenticationPrincipal User user) {
        return userAllergenRepository.findSevereAllergens(user.getId());
    }
    
    // ========================================================================
    // QUERIES - Dietary Preferences
    // ========================================================================
    
    @QueryMapping
    public UserDietaryPreferences getDietaryPreferences(@AuthenticationPrincipal User user) {
        return userDietaryPreferencesRepository.findByUserId(user.getId()).orElse(null);
    }
    
    // ========================================================================
    // QUERIES - Allergen Alerts
    // ========================================================================
    
    @QueryMapping
    public List<AllergenAlert> getAllergenAlerts(@AuthenticationPrincipal User user) {
        return allergenAlertRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
    
    @QueryMapping
    public List<AllergenAlert> getPendingAlerts(@AuthenticationPrincipal User user) {
        return allergenAlertRepository.findByUserIdAndStatus(
            user.getId(), 
            AlertStatus.PENDING
        );
    }
    
    @QueryMapping
    public List<AllergenAlert> getSevereAlerts(@AuthenticationPrincipal User user) {
        return allergenAlertRepository.findSevereAlerts(user.getId());
    }
    
    @QueryMapping
    public List<AllergenAlert> getRecentAlerts(@AuthenticationPrincipal User user) {
        return allergenAlertRepository.findRecentAlerts(user.getId());
    }
    
    // ========================================================================
    // QUERIES - Consumption Logs
    // ========================================================================
    
    @QueryMapping
    public List<ConsumptionLog> getConsumptionLogs(@AuthenticationPrincipal User user) {
        return consumptionLogRepository.findByUserIdOrderByConsumedAtDesc(user.getId());
    }
    
    @QueryMapping
    public List<ConsumptionLog> getTodayConsumption(@AuthenticationPrincipal User user) {
        LocalDate today = LocalDate.now();
        return consumptionLogRepository.findByUserIdAndDateRange(
            user.getId(), 
            today.atStartOfDay(), 
            today.atTime(23, 59, 59)
        );
    }
    
    @QueryMapping
    public List<ConsumptionLog> getConsumptionByDate(
        @AuthenticationPrincipal User user,
        @Argument String date
    ) {
        LocalDate localDate = LocalDate.parse(date);
        return consumptionLogRepository.findByUserIdAndDateRange(
            user.getId(), 
            localDate.atStartOfDay(), 
            localDate.atTime(23, 59, 59)
        );
    }
    
    @QueryMapping
    public List<ConsumptionLog> getConsumptionByDateRange(
        @AuthenticationPrincipal User user,
        @Argument String startDate,
        @Argument String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return consumptionLogRepository.findByUserIdAndDateRange(user.getId(), start.atStartOfDay(), end.atTime(23, 59, 59));
    }
    
    @QueryMapping
    public List<ConsumptionLog> getRecentConsumption(@AuthenticationPrincipal User user) {
        return consumptionLogRepository.findRecentLogs(user.getId());
    }
    
    // ========================================================================
    // QUERIES - Daily Summaries
    // ========================================================================
    
    @QueryMapping
    public DailyNutritionSummary getDailySummary(
        @AuthenticationPrincipal User user,
        @Argument String date
    ) {
        LocalDate localDate = LocalDate.parse(date);
        return dailyNutritionSummaryRepository.findByUserIdAndDate(user.getId(), localDate).orElse(null);
    }
    
    @QueryMapping
    public DailyNutritionSummary getTodaySummary(@AuthenticationPrincipal User user) {
        return dailyNutritionSummaryRepository.findByUserIdAndDate(user.getId(), LocalDate.now()).orElse(null);
    }
    
    @QueryMapping
    public List<DailyNutritionSummary> getCurrentWeekSummaries(@AuthenticationPrincipal User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        return dailyNutritionSummaryRepository.findByUserIdAndDateBetween(user.getId(), startDate, endDate);
    }
    
    @QueryMapping
    public List<DailyNutritionSummary> getCurrentMonthSummaries(@AuthenticationPrincipal User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        return dailyNutritionSummaryRepository.findByUserIdAndDateBetween(user.getId(), startDate, endDate);
    }
    
    @QueryMapping
    public List<DailyNutritionSummary> getSummariesByDateRange(
        @AuthenticationPrincipal User user,
        @Argument String startDate,
        @Argument String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return dailyNutritionSummaryRepository.findByUserIdAndDateBetween(user.getId(), start, end);
    }
    
    // ========================================================================
    // QUERIES - Insights
    // ========================================================================
    
    @QueryMapping
    public Map<String, Object> getNutritionInsights(
        @AuthenticationPrincipal User user,
        @Argument String startDate,
        @Argument String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return nutritionService.getNutritionInsights(user.getId(), start, end);
    }
    
    @QueryMapping
    public Map<String, Object> getWeeklyInsights(@AuthenticationPrincipal User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        return nutritionService.getNutritionInsights(user.getId(), startDate, endDate);
    }
    
    @QueryMapping
    public Map<String, Object> getMonthlyInsights(@AuthenticationPrincipal User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        return nutritionService.getNutritionInsights(user.getId(), startDate, endDate);
    }
    
    // ========================================================================
    // MUTATIONS - Nutrition Info
    // ========================================================================
    
    @MutationMapping
    public NutritionInfo addNutritionInfo(
        @AuthenticationPrincipal User user,
        @Argument Map<String, Object> input
    ) {
        Long itemId = Long.parseLong(input.get("itemId").toString());
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        NutritionInfo nutritionInfo = new NutritionInfo();
        nutritionInfo.setItemId(item.getId());
        
        // Set all fields from input
        if (input.containsKey("servingSize")) {
            nutritionInfo.setServingSize(input.get("servingSize").toString());
        }
        if (input.containsKey("servingUnit")) {
            nutritionInfo.setServingUnit(input.get("servingUnit").toString());
        }
        if (input.containsKey("servingsPerContainer")) {
            nutritionInfo.setServingsPerContainer(new BigDecimal(input.get("servingsPerContainer").toString()));
        }
        if (input.containsKey("calories")) {
            nutritionInfo.setCalories(Integer.parseInt(input.get("calories").toString()));
        }
        if (input.containsKey("protein")) {
            nutritionInfo.setProtein(new BigDecimal(input.get("protein").toString()));
        }
        if (input.containsKey("totalCarbohydrates")) {
            nutritionInfo.setTotalCarbohydrates(new BigDecimal(input.get("totalCarbohydrates").toString()));
        }
        if (input.containsKey("totalFat")) {
            nutritionInfo.setTotalFat(new BigDecimal(input.get("totalFat").toString()));
        }
        // ... set other fields as needed
        
        return nutritionInfoRepository.save(nutritionInfo);
    }
    
    @MutationMapping
    public Boolean deleteNutritionInfo(@Argument Long id) {
        nutritionInfoRepository.deleteById(id);
        return true;
    }
    
    // ========================================================================
    // MUTATIONS - User Allergens
    // ========================================================================
    
    @MutationMapping
    public UserAllergen addUserAllergen(
        @AuthenticationPrincipal User user,
        @Argument Map<String, Object> input
    ) {
        UserAllergen userAllergen = new UserAllergen();
        userAllergen.setUserId(user.getId());
        userAllergen.setAllergenType(
            AllergenType.valueOf(input.get("allergenType").toString())
        );
        userAllergen.setSeverity(
            Severity.valueOf(input.get("severity").toString())
        );
        
        if (input.containsKey("customAllergenName")) {
            userAllergen.setCustomAllergenName(input.get("customAllergenName").toString());
        }
        if (input.containsKey("notes")) {
            userAllergen.setNotes(input.get("notes").toString());
        }
        
        return userAllergenRepository.save(userAllergen);
    }
    
    @MutationMapping
    public UserAllergen updateUserAllergen(
        @Argument Long id,
        @Argument Map<String, Object> input
    ) {
        UserAllergen userAllergen = userAllergenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User allergen not found"));
        
        if (input.containsKey("severity")) {
            userAllergen.setSeverity(
                Severity.valueOf(input.get("severity").toString())
            );
        }
        if (input.containsKey("notes")) {
            userAllergen.setNotes(input.get("notes").toString());
        }
        
        return userAllergenRepository.save(userAllergen);
    }
    
    @MutationMapping
    public Boolean removeUserAllergen(@Argument Long id) {
        userAllergenRepository.deleteById(id);
        return true;
    }
    
    // ========================================================================
    // MUTATIONS - Dietary Preferences
    // ========================================================================
    
    @MutationMapping
    public UserDietaryPreferences updateDietaryPreferences(
        @AuthenticationPrincipal User user,
        @Argument Map<String, Object> input
    ) {
        UserDietaryPreferences prefs = userDietaryPreferencesRepository.findByUserId(user.getId()).orElse(null);
        
        if (prefs == null) {
            prefs = new UserDietaryPreferences();
            prefs.setUserId(user.getId());
        }
        
        // Update dietary restrictions
        if (input.containsKey("isVegan")) {
            prefs.setIsVegan((Boolean) input.get("isVegan"));
        }
        if (input.containsKey("isVegetarian")) {
            prefs.setIsVegetarian((Boolean) input.get("isVegetarian"));
        }
        if (input.containsKey("isGlutenFree")) {
            prefs.setIsGlutenFree((Boolean) input.get("isGlutenFree"));
        }
        if (input.containsKey("isDairyFree")) {
            prefs.setIsDairyFree((Boolean) input.get("isDairyFree"));
        }
        if (input.containsKey("isNutFree")) {
            prefs.setIsNutFree((Boolean) input.get("isNutFree"));
        }
        if (input.containsKey("isKosher")) {
            prefs.setIsKosher((Boolean) input.get("isKosher"));
        }
        if (input.containsKey("isHalal")) {
            prefs.setIsHalal((Boolean) input.get("isHalal"));
        }
        if (input.containsKey("isLowCarb")) {
            prefs.setIsLowCarb((Boolean) input.get("isLowCarb"));
        }
        if (input.containsKey("isKeto")) {
            prefs.setIsKeto((Boolean) input.get("isKeto"));
        }
        if (input.containsKey("isPaleo")) {
            prefs.setIsPaleo((Boolean) input.get("isPaleo"));
        }
        
        // Update nutritional goals
        if (input.containsKey("dailyCalorieGoal")) {
            prefs.setDailyCalorieGoal(Integer.parseInt(input.get("dailyCalorieGoal").toString()));
        }
        if (input.containsKey("dailyProteinGoal")) {
            prefs.setDailyProteinGoal(new BigDecimal(input.get("dailyProteinGoal").toString()));
        }
        if (input.containsKey("dailyCarbGoal")) {
            prefs.setDailyCarbGoal(new BigDecimal(input.get("dailyCarbGoal").toString()));
        }
        if (input.containsKey("dailyFatGoal")) {
            prefs.setDailyFatGoal(new BigDecimal(input.get("dailyFatGoal").toString()));
        }
        
        return userDietaryPreferencesRepository.save(prefs);
    }
    
    // ========================================================================
    // MUTATIONS - Consumption Logs
    // ========================================================================
    
    @MutationMapping
    public ConsumptionLog logConsumption(
        @AuthenticationPrincipal User user,
        @Argument Map<String, Object> input
    ) {
        Long itemId = Long.parseLong(input.get("itemId").toString());
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        BigDecimal servings = new BigDecimal(input.get("servingsConsumed").toString());
        ConsumptionLog.MealType mealType = ConsumptionLog.MealType.valueOf(
            input.get("mealType").toString()
        );
        
        ConsumptionLog log = nutritionService.logConsumption(user.getId(), item.getId(), servings, mealType);
        
        if (input.containsKey("notes")) {
            log.setNotes(input.get("notes").toString());
            log = consumptionLogRepository.save(log);
        }
        
        return log;
    }
    
    @MutationMapping
    public Boolean deleteConsumptionLog(@Argument Long id) {
        consumptionLogRepository.deleteById(id);
        return true;
    }
    
    // ========================================================================
    // MUTATIONS - Allergen Alerts
    // ========================================================================
    
    @MutationMapping
    public AllergenAlert acknowledgeAllergenAlert(
        @Argument Long alertId,
        @Argument String action
    ) {
        AllergenAlert alert = allergenAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        alert.setUserAction(UserAction.valueOf(action));
        alert.setUserActionAt(LocalDateTime.now());
        
        return allergenAlertRepository.save(alert);
    }
    
    @MutationMapping
    public Boolean dismissAlert(@Argument Long alertId) {
        AllergenAlert alert = allergenAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        alert.setStatus(AlertStatus.DISMISSED);
        allergenAlertRepository.save(alert);
        
        return true;
    }
    
    // ========================================================================
    // MUTATIONS - Scanning
    // ========================================================================
    
    @MutationMapping
    public Map<String, Object> scanNutritionLabel(
        @AuthenticationPrincipal User user,
        @Argument Long itemId,
        @Argument String ocrText
    ) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
            
            // Parse nutrition label
            NutritionInfo nutritionInfo = enhancedOCRService.parseNutritionLabel(ocrText);
            if (nutritionInfo != null) {
                nutritionInfo.setItemId(item.getId());
                nutritionInfo = nutritionInfoRepository.save(nutritionInfo);
                result.put("nutritionInfo", nutritionInfo);
            }
            
            // Parse ingredients
            List<Ingredient> ingredients = enhancedOCRService.parseIngredientsList(ocrText);
            result.put("ingredients", ingredients);
            
            // Save item ingredients
            int position = 1;
            for (Ingredient ingredient : ingredients) {
                ItemIngredient itemIngredient = new ItemIngredient();
                itemIngredient.setItemId(item.getId());
                itemIngredient.setIngredientId(ingredient.getId());
                itemIngredient.setPosition(position++);
                itemIngredientRepository.save(itemIngredient);
            }
            
            // Check for allergens
            List<Long> ingredientIds = ingredients.stream().map(Ingredient::getId).collect(Collectors.toList());
            List<AllergenAlert> alerts = nutritionService.checkForAllergens(user.getId(), item.getId(), ingredientIds);
            result.put("allergenAlerts", alerts);
            
            // Check dietary violations
            Map<String, Boolean> violations = nutritionService.checkDietaryRestrictions(user.getId(), ingredientIds);
            List<String> violationList = violations.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            result.put("dietaryViolations", violationList);
            
            result.put("success", true);
            result.put("message", "Nutrition label scanned successfully");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error scanning nutrition label: " + e.getMessage());
        }
        
        return result;
    }
    
    @MutationMapping
    public List<Ingredient> scanIngredients(
        @Argument Long itemId,
        @Argument String ocrText
    ) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        List<Ingredient> ingredients = enhancedOCRService.parseIngredientsList(ocrText);
        
        // Save item ingredients
        int position = 1;
        for (Ingredient ingredient : ingredients) {
            ItemIngredient itemIngredient = new ItemIngredient();
            itemIngredient.setItemId(item.getId());
            itemIngredient.setIngredientId(ingredient.getId());
            itemIngredient.setPosition(position++);
            itemIngredientRepository.save(itemIngredient);
        }
        
        return ingredients;
    }
    
    // ========================================================================
    // MUTATIONS - Ingredients
    // ========================================================================
    
    @MutationMapping
    public Ingredient addIngredient(
        @Argument String name,
        @Argument Boolean isAllergen,
        @Argument String allergenType
    ) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setIsAllergen(isAllergen != null ? isAllergen : false);
        
        if (allergenType != null) {
            ingredient.setAllergenType(AllergenType.valueOf(allergenType));
        }
        
        return ingredientRepository.save(ingredient);
    }
}
