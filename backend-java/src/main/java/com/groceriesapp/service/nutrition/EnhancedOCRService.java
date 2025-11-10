package com.groceriesapp.service.nutrition;

import com.groceriesapp.model.nutrition.AllergenType;
import com.groceriesapp.model.nutrition.Ingredient;
import com.groceriesapp.model.nutrition.NutritionInfo;
import com.groceriesapp.repository.nutrition.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced OCR Service for Nutrition Label Scanning
 * Provides advanced text parsing for nutrition facts and ingredients
 */
@Service
public class EnhancedOCRService {
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    // Nutrition label patterns
    private static final Pattern CALORIES_PATTERN = Pattern.compile("Calories[:\\s]+([0-9]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SERVING_SIZE_PATTERN = Pattern.compile("Serving Size[:\\s]+([^\\n]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SERVINGS_PER_CONTAINER_PATTERN = Pattern.compile("Servings Per Container[:\\s]+([0-9.]+)", Pattern.CASE_INSENSITIVE);
    
    // Nutrient patterns (handles various formats)
    private static final Map<String, Pattern> NUTRIENT_PATTERNS = new HashMap<>();
    
    static {
        // Total Fat: 8g or Total Fat 8 g or Total Fat 8g
        NUTRIENT_PATTERNS.put("totalFat", Pattern.compile("Total Fat[:\\s]+([0-9.]+)\\s*g", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("saturatedFat", Pattern.compile("Saturated Fat[:\\s]+([0-9.]+)\\s*g", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("transFat", Pattern.compile("Trans Fat[:\\s]+([0-9.]+)\\s*g", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("cholesterol", Pattern.compile("Cholesterol[:\\s]+([0-9.]+)\\s*mg", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("sodium", Pattern.compile("Sodium[:\\s]+([0-9.]+)\\s*mg", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("totalCarbohydrates", Pattern.compile("Total Carbohydrate[s]?[:\\s]+([0-9.]+)\\s*g", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("dietaryFiber", Pattern.compile("Dietary Fiber[:\\s]+([0-9.]+)\\s*g", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("totalSugars", Pattern.compile("Total Sugars[:\\s]+([0-9.]+)\\s*g", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("addedSugars", Pattern.compile("Added Sugars[:\\s]+([0-9.]+)\\s*g", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("protein", Pattern.compile("Protein[:\\s]+([0-9.]+)\\s*g", Pattern.CASE_INSENSITIVE));
        
        // Vitamins and Minerals
        NUTRIENT_PATTERNS.put("vitaminD", Pattern.compile("Vitamin D[:\\s]+([0-9.]+)\\s*(mcg|µg)", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("calcium", Pattern.compile("Calcium[:\\s]+([0-9.]+)\\s*mg", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("iron", Pattern.compile("Iron[:\\s]+([0-9.]+)\\s*mg", Pattern.CASE_INSENSITIVE));
        NUTRIENT_PATTERNS.put("potassium", Pattern.compile("Potassium[:\\s]+([0-9.]+)\\s*mg", Pattern.CASE_INSENSITIVE));
    }
    
    /**
     * Parse nutrition label from OCR text with enhanced accuracy
     */
    public NutritionInfo parseNutritionLabel(String ocrText) {
        if (ocrText == null || ocrText.trim().isEmpty()) {
            return null;
        }
        
        NutritionInfo nutritionInfo = new NutritionInfo();
        
        // Normalize text (remove extra whitespace, standardize line breaks)
        String normalizedText = normalizeText(ocrText);
        
        // Parse serving information
        parseServingInfo(normalizedText, nutritionInfo);
        
        // Parse calories
        parseCalories(normalizedText, nutritionInfo);
        
        // Parse all nutrients
        parseNutrients(normalizedText, nutritionInfo);
        
        return nutritionInfo;
    }
    
    /**
     * Parse ingredients list from OCR text
     */
    public List<Ingredient> parseIngredientsList(String ocrText) {
        if (ocrText == null || ocrText.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Ingredient> ingredients = new ArrayList<>();
        
        // Find ingredients section
        String ingredientsText = extractIngredientsSection(ocrText);
        if (ingredientsText == null || ingredientsText.isEmpty()) {
            return ingredients;
        }
        
        // Parse ingredients
        List<String> ingredientNames = splitIngredients(ingredientsText);
        
        int position = 1;
        for (String ingredientName : ingredientNames) {
            Ingredient ingredient = findOrCreateIngredient(ingredientName);
            if (ingredient != null) {
                ingredients.add(ingredient);
                position++;
            }
        }
        
        return ingredients;
    }
    
    /**
     * Detect allergens in ingredients list
     */
    public List<Ingredient> detectAllergens(List<Ingredient> ingredients) {
        return ingredients.stream()
                .filter(ingredient -> ingredient.getIsAllergen() != null && ingredient.getIsAllergen())
                .toList();
    }
    
    /**
     * Extract nutrition facts section from text
     */
    public String extractNutritionSection(String text) {
        // Look for "Nutrition Facts" header
        int startIndex = text.toLowerCase().indexOf("nutrition facts");
        if (startIndex == -1) {
            startIndex = text.toLowerCase().indexOf("nutrition information");
        }
        if (startIndex == -1) {
            return text; // Return whole text if no header found
        }
        
        // Find end of nutrition section (usually at ingredients or end of text)
        int endIndex = text.toLowerCase().indexOf("ingredients:", startIndex);
        if (endIndex == -1) {
            endIndex = text.length();
        }
        
        return text.substring(startIndex, endIndex);
    }
    
    // Private helper methods
    
    private String normalizeText(String text) {
        // Remove multiple spaces
        text = text.replaceAll("\\s+", " ");
        
        // Standardize line breaks
        text = text.replaceAll("\\r\\n", "\n");
        text = text.replaceAll("\\r", "\n");
        
        // Remove special characters that might interfere with parsing
        text = text.replaceAll("[*•]", "");
        
        return text.trim();
    }
    
    private void parseServingInfo(String text, NutritionInfo nutritionInfo) {
        // Parse serving size
        Matcher servingSizeMatcher = SERVING_SIZE_PATTERN.matcher(text);
        if (servingSizeMatcher.find()) {
            String servingSize = servingSizeMatcher.group(1).trim();
            nutritionInfo.setServingSize(servingSize);
            
            // Extract unit if possible (e.g., "1 cup (240ml)" -> unit = "cup")
            String[] parts = servingSize.split("\\s+");
            if (parts.length >= 2) {
                nutritionInfo.setServingUnit(parts[1].replaceAll("[()]", ""));
            }
        }
        
        // Parse servings per container
        Matcher servingsPerContainerMatcher = SERVINGS_PER_CONTAINER_PATTERN.matcher(text);
        if (servingsPerContainerMatcher.find()) {
            try {
                BigDecimal servingsPerContainer = new BigDecimal(servingsPerContainerMatcher.group(1));
                nutritionInfo.setServingsPerContainer(servingsPerContainer);
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }
    }
    
    private void parseCalories(String text, NutritionInfo nutritionInfo) {
        Matcher caloriesMatcher = CALORIES_PATTERN.matcher(text);
        if (caloriesMatcher.find()) {
            try {
                int calories = Integer.parseInt(caloriesMatcher.group(1));
                nutritionInfo.setCalories(calories);
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }
        
        // Try to find calories from fat
        Pattern caloriesFromFatPattern = Pattern.compile("Calories from Fat[:\\s]+([0-9]+)", Pattern.CASE_INSENSITIVE);
        Matcher caloriesFromFatMatcher = caloriesFromFatPattern.matcher(text);
        if (caloriesFromFatMatcher.find()) {
            try {
                int caloriesFromFat = Integer.parseInt(caloriesFromFatMatcher.group(1));
                nutritionInfo.setCaloriesFromFat(caloriesFromFat);
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }
    }
    
    private void parseNutrients(String text, NutritionInfo nutritionInfo) {
        for (Map.Entry<String, Pattern> entry : NUTRIENT_PATTERNS.entrySet()) {
            String nutrientName = entry.getKey();
            Pattern pattern = entry.getValue();
            
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    BigDecimal value = new BigDecimal(matcher.group(1));
                    setNutrientValue(nutritionInfo, nutrientName, value);
                } catch (NumberFormatException e) {
                    // Ignore invalid numbers
                }
            }
        }
    }
    
    private void setNutrientValue(NutritionInfo nutritionInfo, String nutrientName, BigDecimal value) {
        switch (nutrientName) {
            case "totalFat":
                nutritionInfo.setTotalFat(value);
                break;
            case "saturatedFat":
                nutritionInfo.setSaturatedFat(value);
                break;
            case "transFat":
                nutritionInfo.setTransFat(value);
                break;
            case "cholesterol":
                nutritionInfo.setCholesterol(value);
                break;
            case "sodium":
                nutritionInfo.setSodium(value);
                break;
            case "totalCarbohydrates":
                nutritionInfo.setTotalCarbohydrates(value);
                break;
            case "dietaryFiber":
                nutritionInfo.setDietaryFiber(value);
                break;
            case "totalSugars":
                nutritionInfo.setTotalSugars(value);
                break;
            case "addedSugars":
                nutritionInfo.setAddedSugars(value);
                break;
            case "protein":
                nutritionInfo.setProtein(value);
                break;
            case "vitaminD":
                nutritionInfo.setVitaminD(value);
                break;
            case "calcium":
                nutritionInfo.setCalcium(value);
                break;
            case "iron":
                nutritionInfo.setIron(value);
                break;
            case "potassium":
                nutritionInfo.setPotassium(value);
                break;
        }
    }
    
    private String extractIngredientsSection(String text) {
        // Look for "Ingredients:" header
        int startIndex = text.toLowerCase().indexOf("ingredients:");
        if (startIndex == -1) {
            return null;
        }
        
        startIndex += "ingredients:".length();
        
        // Find end of ingredients section (usually at next section or end of text)
        int endIndex = text.indexOf("\n\n", startIndex);
        if (endIndex == -1) {
            // Try to find other section headers
            String lowerText = text.toLowerCase();
            int allergenIndex = lowerText.indexOf("contains:", startIndex);
            int mayContainIndex = lowerText.indexOf("may contain:", startIndex);
            int nutritionIndex = lowerText.indexOf("nutrition", startIndex);
            
            endIndex = text.length();
            if (allergenIndex != -1 && allergenIndex < endIndex) endIndex = allergenIndex;
            if (mayContainIndex != -1 && mayContainIndex < endIndex) endIndex = mayContainIndex;
            if (nutritionIndex != -1 && nutritionIndex < endIndex) endIndex = nutritionIndex;
        }
        
        return text.substring(startIndex, endIndex).trim();
    }
    
    private List<String> splitIngredients(String ingredientsText) {
        List<String> ingredients = new ArrayList<>();
        
        // Split by comma, but be smart about parentheses
        List<String> parts = smartSplit(ingredientsText, ',');
        
        for (String part : parts) {
            String cleaned = cleanIngredientName(part);
            if (!cleaned.isEmpty()) {
                ingredients.add(cleaned);
            }
        }
        
        return ingredients;
    }
    
    private List<String> smartSplit(String text, char delimiter) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int parenthesesDepth = 0;
        
        for (char c : text.toCharArray()) {
            if (c == '(') {
                parenthesesDepth++;
                current.append(c);
            } else if (c == ')') {
                parenthesesDepth--;
                current.append(c);
            } else if (c == delimiter && parenthesesDepth == 0) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            result.add(current.toString());
        }
        
        return result;
    }
    
    private String cleanIngredientName(String name) {
        // Remove parentheses content (usually contains sub-ingredients)
        name = name.replaceAll("\\([^)]*\\)", "");
        
        // Remove percentages
        name = name.replaceAll("[0-9]+%", "");
        
        // Remove "and", "or" at the beginning
        name = name.replaceAll("^(and|or)\\s+", "");
        
        // Trim and lowercase
        name = name.trim().toLowerCase();
        
        // Remove trailing punctuation
        name = name.replaceAll("[.,;]+$", "");
        
        return name;
    }
    
    private Ingredient findOrCreateIngredient(String ingredientName) {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            return null;
        }
        
        // Try to find existing ingredient
        Optional<Ingredient> ingredientOpt = ingredientRepository.findByNameIgnoreCase(ingredientName);
        Ingredient ingredient = null;
        
        if (ingredientOpt.isPresent()) {
            ingredient = ingredientOpt.get();
            return ingredient;
        }
        
        // Check if it matches any aliases
        ingredient = findIngredientByAlias(ingredientName);
        if (ingredient != null) {
            return ingredient;
        }
        
        // Create new ingredient
        ingredient = new Ingredient();
        ingredient.setName(ingredientName);
        ingredient.setIsAllergen(false);
        ingredient.setIsVegan(true);
        ingredient.setIsVegetarian(true);
        ingredient.setIsGlutenFree(true);
        ingredient.setIsDairyFree(true);
        ingredient.setIsNutFree(true);
        
        // Try to detect if it's a known allergen based on name
        detectAllergenFromName(ingredient, ingredientName);
        
        return ingredientRepository.save(ingredient);
    }
    
    private Ingredient findIngredientByAlias(String name) {
        List<Ingredient> allIngredients = ingredientRepository.findAll();
        
        for (Ingredient ingredient : allIngredients) {
            if (ingredient.getAliases() != null && 
                ingredient.getAliases().toLowerCase().contains("\"" + name.toLowerCase() + "\"")) {
                return ingredient;
            }
        }
        
        return null;
    }
    
    private void detectAllergenFromName(Ingredient ingredient, String name) {
        String lowerName = name.toLowerCase();
        
        // Milk/Dairy
        if (lowerName.contains("milk") || lowerName.contains("dairy") || 
            lowerName.contains("cream") || lowerName.contains("butter") || 
            lowerName.contains("cheese") || lowerName.contains("lactose")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.MILK);
            ingredient.setIsDairyFree(false);
            ingredient.setIsVegan(false);
        }
        
        // Eggs
        if (lowerName.contains("egg")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.EGGS);
            ingredient.setIsVegan(false);
        }
        
        // Wheat/Gluten
        if (lowerName.contains("wheat") || lowerName.contains("flour")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.WHEAT);
            ingredient.setIsGlutenFree(false);
        }
        
        // Soy
        if (lowerName.contains("soy") || lowerName.contains("soya")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.SOYBEANS);
        }
        
        // Nuts
        if (lowerName.contains("nut") || lowerName.contains("almond") || 
            lowerName.contains("walnut") || lowerName.contains("cashew") || 
            lowerName.contains("pecan") || lowerName.contains("pistachio")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.TREE_NUTS);
            ingredient.setIsNutFree(false);
        }
        
        // Peanuts
        if (lowerName.contains("peanut") || lowerName.contains("groundnut")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.PEANUTS);
            ingredient.setIsNutFree(false);
        }
        
        // Fish
        if (lowerName.contains("fish") || lowerName.contains("salmon") || 
            lowerName.contains("tuna") || lowerName.contains("cod")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.FISH);
            ingredient.setIsVegan(false);
            ingredient.setIsVegetarian(false);
        }
        
        // Shellfish
        if (lowerName.contains("shrimp") || lowerName.contains("crab") || 
            lowerName.contains("lobster") || lowerName.contains("clam") || 
            lowerName.contains("oyster")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.SHELLFISH);
            ingredient.setIsVegan(false);
            ingredient.setIsVegetarian(false);
        }
        
        // Sesame
        if (lowerName.contains("sesame") || lowerName.contains("tahini")) {
            ingredient.setIsAllergen(true);
            ingredient.setAllergenType(AllergenType.SESAME);
        }
    }
}
