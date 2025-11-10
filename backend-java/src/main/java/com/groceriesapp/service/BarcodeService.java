package com.groceriesapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for barcode scanning and product lookup.
 * Integrates with Open Food Facts API for product information.
 */
@Service
public class BarcodeService {
    
    private static final String OPEN_FOOD_FACTS_API = "https://world.openfoodfacts.org/api/v0/product/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public BarcodeService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Scan barcode and retrieve product information.
     * 
     * @param barcode The product barcode (UPC, EAN, etc.)
     * @return Map containing product information
     */
    public Map<String, Object> scanBarcode(String barcode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Call Open Food Facts API
            String url = OPEN_FOOD_FACTS_API + barcode + ".json";
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null) {
                result.put("success", false);
                result.put("message", "Product not found");
                return result;
            }
            
            // Parse JSON response
            JsonNode root = objectMapper.readTree(response);
            
            // Check if product was found
            int status = root.path("status").asInt(0);
            if (status == 0) {
                result.put("success", false);
                result.put("message", "Product not found in database");
                return result;
            }
            
            JsonNode product = root.path("product");
            
            // Extract product information
            String productName = product.path("product_name").asText("");
            String brand = product.path("brands").asText("");
            String category = product.path("categories").asText("");
            String imageUrl = product.path("image_url").asText("");
            
            // Determine storage location based on category
            String storageLocation = determineStorageLocation(category);
            
            // Estimate shelf life based on category
            Integer estimatedShelfLife = estimateShelfLife(category, productName);
            
            // Build result
            result.put("success", true);
            result.put("barcode", barcode);
            result.put("name", brand.isEmpty() ? productName : brand + " " + productName);
            result.put("brand", brand);
            result.put("category", category);
            result.put("imageUrl", imageUrl);
            result.put("storageLocation", storageLocation);
            result.put("estimatedShelfLifeDays", estimatedShelfLife);
            
            // Additional nutritional info (optional)
            if (product.has("nutriments")) {
                JsonNode nutriments = product.path("nutriments");
                Map<String, Object> nutrition = new HashMap<>();
                nutrition.put("calories", nutriments.path("energy-kcal_100g").asDouble(0));
                nutrition.put("protein", nutriments.path("proteins_100g").asDouble(0));
                nutrition.put("carbs", nutriments.path("carbohydrates_100g").asDouble(0));
                nutrition.put("fat", nutriments.path("fat_100g").asDouble(0));
                result.put("nutrition", nutrition);
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error scanning barcode: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Determine appropriate storage location based on product category.
     */
    private String determineStorageLocation(String category) {
        String lowerCategory = category.toLowerCase();
        
        if (lowerCategory.contains("frozen") || lowerCategory.contains("ice cream")) {
            return "Freezer";
        } else if (lowerCategory.contains("dairy") || lowerCategory.contains("meat") || 
                   lowerCategory.contains("fish") || lowerCategory.contains("refrigerated") ||
                   lowerCategory.contains("yogurt") || lowerCategory.contains("cheese")) {
            return "Fridge";
        } else if (lowerCategory.contains("fresh") || lowerCategory.contains("produce") ||
                   lowerCategory.contains("fruit") || lowerCategory.contains("vegetable")) {
            return "Fridge";
        } else {
            return "Pantry";
        }
    }
    
    /**
     * Estimate shelf life in days based on product category and name.
     */
    private Integer estimateShelfLife(String category, String productName) {
        String lowerCategory = category.toLowerCase();
        String lowerName = productName.toLowerCase();
        
        // Frozen foods
        if (lowerCategory.contains("frozen")) {
            return 180; // 6 months
        }
        
        // Dairy
        if (lowerCategory.contains("dairy") || lowerName.contains("milk")) {
            if (lowerName.contains("cheese")) return 30;
            if (lowerName.contains("yogurt")) return 14;
            return 7; // Default for dairy
        }
        
        // Meat & Fish
        if (lowerCategory.contains("meat") || lowerCategory.contains("fish") ||
            lowerCategory.contains("poultry")) {
            return 3;
        }
        
        // Fresh produce
        if (lowerCategory.contains("fruit") || lowerCategory.contains("vegetable")) {
            if (lowerName.contains("apple") || lowerName.contains("potato")) return 30;
            if (lowerName.contains("banana") || lowerName.contains("tomato")) return 7;
            return 10; // Default for produce
        }
        
        // Bread
        if (lowerCategory.contains("bread") || lowerCategory.contains("bakery")) {
            return 7;
        }
        
        // Canned/Packaged foods
        if (lowerCategory.contains("canned") || lowerCategory.contains("packaged")) {
            return 365; // 1 year
        }
        
        // Default
        return 30;
    }
}
