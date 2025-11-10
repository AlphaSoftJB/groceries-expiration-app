package com.groceriesapp.service.ml;

import com.groceriesapp.model.ml.ExpirationPrediction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ExpirationPredictionService {
    
    private static final String MODEL_VERSION = "1.0.0";
    
    // Category-based shelf life averages (in days)
    private static final Map<String, Integer> CATEGORY_SHELF_LIFE = new HashMap<>() {{
        put("DAIRY", 7);
        put("MEAT", 3);
        put("SEAFOOD", 2);
        put("VEGETABLES", 7);
        put("FRUITS", 5);
        put("BREAD", 5);
        put("EGGS", 21);
        put("CONDIMENTS", 180);
        put("CANNED", 365);
        put("FROZEN", 90);
        put("BEVERAGES", 30);
        put("SNACKS", 60);
    }};
    
    // Storage location modifiers (multiplier for shelf life)
    private static final Map<String, Double> STORAGE_MODIFIERS = new HashMap<>() {{
        put("FRIDGE", 1.0);
        put("FREEZER", 3.0);
        put("PANTRY", 0.8);
        put("COUNTER", 0.6);
    }};
    
    /**
     * Predict expiration date using machine learning model
     */
    public ExpirationPrediction predictExpirationDate(PredictionInput input) {
        ExpirationPrediction prediction = new ExpirationPrediction();
        
        // Extract features
        Map<String, Double> features = extractFeatures(input);
        
        // Calculate base prediction
        int baseShelfLife = CATEGORY_SHELF_LIFE.getOrDefault(input.getCategory(), 14);
        double storageModifier = STORAGE_MODIFIERS.getOrDefault(input.getStorageLocation(), 1.0);
        
        // Apply environmental factors
        double temperatureFactor = calculateTemperatureFactor(input.getTemperatureAvg());
        double humidityFactor = calculateHumidityFactor(input.getHumidityAvg());
        double openCountFactor = calculateOpenCountFactor(input.getOpenCount());
        
        // Calculate adjusted shelf life
        double adjustedShelfLife = baseShelfLife * storageModifier * 
                                    temperatureFactor * humidityFactor * openCountFactor;
        
        // Calculate predicted expiration date
        LocalDate predictedDate = input.getPurchaseDate().plusDays((long) adjustedShelfLife);
        
        // Calculate confidence score
        double confidenceScore = calculateConfidenceScore(input, features);
        
        // Build prediction object
        prediction.setItemId(input.getItemId());
        prediction.setCategory(input.getCategory());
        prediction.setStorageLocation(input.getStorageLocation());
        prediction.setPurchaseDate(input.getPurchaseDate());
        prediction.setLabeledExpirationDate(input.getLabeledExpirationDate());
        prediction.setPredictedExpirationDate(predictedDate);
        prediction.setConfidenceScore(confidenceScore);
        prediction.setTemperatureAvg(input.getTemperatureAvg());
        prediction.setHumidityAvg(input.getHumidityAvg());
        prediction.setOpenCount(input.getOpenCount());
        prediction.setModelVersion(MODEL_VERSION);
        prediction.setFeatures(features.toString());
        
        return prediction;
    }
    
    /**
     * Extract features for ML model
     */
    private Map<String, Double> extractFeatures(PredictionInput input) {
        Map<String, Double> features = new HashMap<>();
        
        // Temporal features
        long daysSincePurchase = ChronoUnit.DAYS.between(input.getPurchaseDate(), LocalDate.now());
        features.put("days_since_purchase", (double) daysSincePurchase);
        
        if (input.getLabeledExpirationDate() != null) {
            long labeledShelfLife = ChronoUnit.DAYS.between(
                input.getPurchaseDate(), 
                input.getLabeledExpirationDate()
            );
            features.put("labeled_shelf_life", (double) labeledShelfLife);
        }
        
        // Environmental features
        features.put("temperature_avg", input.getTemperatureAvg() != null ? input.getTemperatureAvg() : 4.0);
        features.put("humidity_avg", input.getHumidityAvg() != null ? input.getHumidityAvg() : 50.0);
        
        // Usage features
        features.put("open_count", input.getOpenCount() != null ? input.getOpenCount().doubleValue() : 0.0);
        
        // Category encoding (one-hot)
        features.put("is_dairy", input.getCategory().equals("DAIRY") ? 1.0 : 0.0);
        features.put("is_meat", input.getCategory().equals("MEAT") ? 1.0 : 0.0);
        features.put("is_produce", 
            (input.getCategory().equals("VEGETABLES") || input.getCategory().equals("FRUITS")) ? 1.0 : 0.0
        );
        
        // Storage encoding
        features.put("is_refrigerated", input.getStorageLocation().equals("FRIDGE") ? 1.0 : 0.0);
        features.put("is_frozen", input.getStorageLocation().equals("FREEZER") ? 1.0 : 0.0);
        
        return features;
    }
    
    /**
     * Calculate temperature factor (optimal fridge temp is 4°C)
     */
    private double calculateTemperatureFactor(Double temperature) {
        if (temperature == null) return 1.0;
        
        // Optimal temperature is 4°C
        double deviation = Math.abs(temperature - 4.0);
        
        if (deviation <= 2.0) {
            return 1.0; // Optimal range
        } else if (deviation <= 5.0) {
            return 0.9; // Slightly suboptimal
        } else if (deviation <= 10.0) {
            return 0.7; // Significantly suboptimal
        } else {
            return 0.5; // Poor storage conditions
        }
    }
    
    /**
     * Calculate humidity factor (optimal is 50-70%)
     */
    private double calculateHumidityFactor(Double humidity) {
        if (humidity == null) return 1.0;
        
        if (humidity >= 50 && humidity <= 70) {
            return 1.0; // Optimal range
        } else if (humidity >= 40 && humidity <= 80) {
            return 0.95; // Acceptable range
        } else if (humidity >= 30 && humidity <= 90) {
            return 0.85; // Suboptimal
        } else {
            return 0.7; // Poor conditions
        }
    }
    
    /**
     * Calculate open count factor (each opening reduces shelf life)
     */
    private double calculateOpenCountFactor(Integer openCount) {
        if (openCount == null || openCount == 0) return 1.0;
        
        // Each opening reduces shelf life by 5%
        double reduction = openCount * 0.05;
        return Math.max(0.5, 1.0 - reduction); // Minimum 50% of original shelf life
    }
    
    /**
     * Calculate confidence score for prediction
     */
    private double calculateConfidenceScore(PredictionInput input, Map<String, Double> features) {
        double confidence = 0.7; // Base confidence
        
        // Increase confidence if we have labeled expiration date
        if (input.getLabeledExpirationDate() != null) {
            confidence += 0.15;
        }
        
        // Increase confidence if we have environmental data
        if (input.getTemperatureAvg() != null && input.getHumidityAvg() != null) {
            confidence += 0.10;
        }
        
        // Increase confidence for well-known categories
        if (CATEGORY_SHELF_LIFE.containsKey(input.getCategory())) {
            confidence += 0.05;
        }
        
        return Math.min(1.0, confidence);
    }
    
    /**
     * Update prediction accuracy after actual expiration
     */
    public void updatePredictionAccuracy(ExpirationPrediction prediction, LocalDate actualExpirationDate) {
        prediction.setActualExpirationDate(actualExpirationDate);
        
        if (prediction.getPredictedExpirationDate() != null) {
            long daysDifference = Math.abs(ChronoUnit.DAYS.between(
                prediction.getPredictedExpirationDate(),
                actualExpirationDate
            ));
            
            // Calculate accuracy (100% if exact, decreases with difference)
            double accuracy = Math.max(0.0, 100.0 - (daysDifference * 5.0));
            prediction.setPredictionAccuracy(accuracy);
        }
    }
    
    /**
     * Get model performance metrics
     */
    public ModelPerformanceMetrics getModelPerformance(List<ExpirationPrediction> predictions) {
        ModelPerformanceMetrics metrics = new ModelPerformanceMetrics();
        
        List<ExpirationPrediction> validPredictions = predictions.stream()
            .filter(p -> p.getActualExpirationDate() != null && p.getPredictedExpirationDate() != null)
            .toList();
        
        if (validPredictions.isEmpty()) {
            return metrics;
        }
        
        // Calculate Mean Absolute Error (MAE)
        double totalError = 0.0;
        double totalSquaredError = 0.0;
        int correctPredictions = 0;
        
        for (ExpirationPrediction prediction : validPredictions) {
            long daysDifference = ChronoUnit.DAYS.between(
                prediction.getPredictedExpirationDate(),
                prediction.getActualExpirationDate()
            );
            
            totalError += Math.abs(daysDifference);
            totalSquaredError += daysDifference * daysDifference;
            
            if (Math.abs(daysDifference) <= 1) {
                correctPredictions++;
            }
        }
        
        metrics.setMeanAbsoluteError(totalError / validPredictions.size());
        metrics.setRootMeanSquaredError(Math.sqrt(totalSquaredError / validPredictions.size()));
        metrics.setAccuracyWithinOneDay((double) correctPredictions / validPredictions.size() * 100);
        metrics.setTotalPredictions(validPredictions.size());
        metrics.setModelVersion(MODEL_VERSION);
        
        return metrics;
    }
    
    /**
     * Prediction Input DTO
     */
    public static class PredictionInput {
        private Long itemId;
        private String category;
        private String storageLocation;
        private LocalDate purchaseDate;
        private LocalDate labeledExpirationDate;
        private Double temperatureAvg;
        private Double humidityAvg;
        private Integer openCount;
        
        // Getters and Setters
        public Long getItemId() {
            return itemId;
        }
        
        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getStorageLocation() {
            return storageLocation;
        }
        
        public void setStorageLocation(String storageLocation) {
            this.storageLocation = storageLocation;
        }
        
        public LocalDate getPurchaseDate() {
            return purchaseDate;
        }
        
        public void setPurchaseDate(LocalDate purchaseDate) {
            this.purchaseDate = purchaseDate;
        }
        
        public LocalDate getLabeledExpirationDate() {
            return labeledExpirationDate;
        }
        
        public void setLabeledExpirationDate(LocalDate labeledExpirationDate) {
            this.labeledExpirationDate = labeledExpirationDate;
        }
        
        public Double getTemperatureAvg() {
            return temperatureAvg;
        }
        
        public void setTemperatureAvg(Double temperatureAvg) {
            this.temperatureAvg = temperatureAvg;
        }
        
        public Double getHumidityAvg() {
            return humidityAvg;
        }
        
        public void setHumidityAvg(Double humidityAvg) {
            this.humidityAvg = humidityAvg;
        }
        
        public Integer getOpenCount() {
            return openCount;
        }
        
        public void setOpenCount(Integer openCount) {
            this.openCount = openCount;
        }
    }
    
    /**
     * Model Performance Metrics DTO
     */
    public static class ModelPerformanceMetrics {
        private double meanAbsoluteError;
        private double rootMeanSquaredError;
        private double accuracyWithinOneDay;
        private int totalPredictions;
        private String modelVersion;
        
        // Getters and Setters
        public double getMeanAbsoluteError() {
            return meanAbsoluteError;
        }
        
        public void setMeanAbsoluteError(double meanAbsoluteError) {
            this.meanAbsoluteError = meanAbsoluteError;
        }
        
        public double getRootMeanSquaredError() {
            return rootMeanSquaredError;
        }
        
        public void setRootMeanSquaredError(double rootMeanSquaredError) {
            this.rootMeanSquaredError = rootMeanSquaredError;
        }
        
        public double getAccuracyWithinOneDay() {
            return accuracyWithinOneDay;
        }
        
        public void setAccuracyWithinOneDay(double accuracyWithinOneDay) {
            this.accuracyWithinOneDay = accuracyWithinOneDay;
        }
        
        public int getTotalPredictions() {
            return totalPredictions;
        }
        
        public void setTotalPredictions(int totalPredictions) {
            this.totalPredictions = totalPredictions;
        }
        
        public String getModelVersion() {
            return modelVersion;
        }
        
        public void setModelVersion(String modelVersion) {
            this.modelVersion = modelVersion;
        }
    }
}
