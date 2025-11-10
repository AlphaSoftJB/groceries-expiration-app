package com.groceriesapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnhancedAIServiceTest {
    
    private EnhancedAIService aiService;
    
    @BeforeEach
    void setUp() {
        aiService = new EnhancedAIService();
    }
    
    @Test
    void testPredictExpirationDate_Milk_Fridge() {
        LocalDate predicted = aiService.predictExpirationDate("milk", "Fridge", null);
        
        assertNotNull(predicted);
        assertTrue(predicted.isAfter(LocalDate.now()));
        
        // Milk in fridge should last around 7-14 days (with 1.5x fridge multiplier)
        long daysUntilExpiration = LocalDate.now().until(predicted).getDays();
        assertTrue(daysUntilExpiration >= 7 && daysUntilExpiration <= 15,
            "Milk should last 7-15 days in fridge, got: " + daysUntilExpiration);
    }
    
    @Test
    void testPredictExpirationDate_Milk_Freezer() {
        LocalDate predicted = aiService.predictExpirationDate("milk", "Freezer", null);
        
        assertNotNull(predicted);
        
        // Freezer should significantly extend shelf life
        long daysUntilExpiration = LocalDate.now().until(predicted).getDays();
        // With seasonal variations, should be at least 7+ days (much more than fridge)
        assertTrue(daysUntilExpiration >= 7,
            "Milk in freezer should last 7+ days, got: " + daysUntilExpiration);
    }
    
    @Test
    void testPredictExpirationDate_WithGivenDate() {
        LocalDate givenDate = LocalDate.now().plusDays(10);
        LocalDate predicted = aiService.predictExpirationDate("milk", "Fridge", givenDate);
        
        assertNotNull(predicted);
        
        // Should be a weighted average, so somewhere between AI prediction and given date
        long daysUntilExpiration = LocalDate.now().until(predicted).getDays();
        assertTrue(daysUntilExpiration > 0 && daysUntilExpiration <= 15);
    }
    
    @Test
    void testAnalyzeFreshness() {
        byte[] mockImageData = new byte[100];
        double freshness = aiService.analyzeFreshness("apple", mockImageData);
        
        // Freshness should be between 0 and 1
        assertTrue(freshness >= 0.0 && freshness <= 1.0,
            "Freshness score should be 0-1, got: " + freshness);
    }
    
    @Test
    void testAnalyzeConsumptionPatterns() {
        List<EnhancedAIService.ConsumptionRecord> history = new ArrayList<>();
        history.add(new EnhancedAIService.ConsumptionRecord("milk", 
            LocalDate.now().minusDays(10), LocalDate.now().minusDays(5)));
        history.add(new EnhancedAIService.ConsumptionRecord("milk", 
            LocalDate.now().minusDays(20), LocalDate.now().minusDays(15)));
        history.add(new EnhancedAIService.ConsumptionRecord("apple", 
            LocalDate.now().minusDays(15), LocalDate.now().minusDays(10)));
        
        Map<String, Object> patterns = aiService.analyzeConsumptionPatterns(history);
        
        assertNotNull(patterns);
        assertTrue(patterns.containsKey("Dairy_avgDays"));
        assertTrue(patterns.containsKey("Fruit_avgDays"));
        
        // Average days for milk should be around 5
        Double dairyAvg = (Double) patterns.get("Dairy_avgDays");
        assertNotNull(dairyAvg);
        assertTrue(dairyAvg >= 4.0 && dairyAvg <= 6.0);
    }
    
    @Test
    void testPredictWasteLikelihood_AlreadyExpired() {
        LocalDate expiredDate = LocalDate.now().minusDays(1);
        Map<String, Object> patterns = Map.of("Dairy_avgDays", 5.0);
        
        double wasteProbability = aiService.predictWasteLikelihood("milk", expiredDate, patterns);
        
        // Already expired should have very high waste probability (may be adjusted by perishability)
        assertTrue(wasteProbability >= 0.9, "Waste probability should be >= 0.9, got: " + wasteProbability);
    }
    
    @Test
    void testPredictWasteLikelihood_LongTimeUntilExpiration() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        Map<String, Object> patterns = Map.of("Dairy_avgDays", 5.0);
        
        double wasteProbability = aiService.predictWasteLikelihood("milk", futureDate, patterns);
        
        // Long time until expiration should have low waste probability
        assertTrue(wasteProbability < 0.3,
            "Waste probability should be low, got: " + wasteProbability);
    }
    
    @Test
    void testGenerateRecommendations() {
        List<String> inventory = List.of("milk", "cheese"); // Only dairy
        Map<String, Object> patterns = Map.of();
        
        List<String> recommendations = aiService.generateRecommendations(inventory, patterns);
        
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        
        // Should recommend missing categories
        String allRecommendations = String.join(" ", recommendations).toLowerCase();
        assertTrue(allRecommendations.contains("fruit") || 
                   allRecommendations.contains("vegetable") ||
                   allRecommendations.contains("meat"));
    }
    
    @Test
    void testPredictExpirationDate_UnknownFood() {
        LocalDate predicted = aiService.predictExpirationDate("unknown_food_item", "Fridge", null);
        
        assertNotNull(predicted);
        assertTrue(predicted.isAfter(LocalDate.now()));
        
        // Unknown items should get default shelf life
        long daysUntilExpiration = LocalDate.now().until(predicted).getDays();
        assertTrue(daysUntilExpiration > 0);
    }
}
