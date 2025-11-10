package com.groceriesapp.service;

import com.groceriesapp.model.VoiceCommand;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VoiceCommandService {
    
    /**
     * Process voice command and extract intent
     */
    public VoiceCommandResult processVoiceCommand(String commandText, Long userId) {
        long startTime = System.currentTimeMillis();
        
        VoiceCommandResult result = new VoiceCommandResult();
        result.setCommandText(commandText);
        result.setUserId(userId);
        
        // Normalize command text
        String normalized = commandText.toLowerCase().trim();
        
        // Detect intent and extract parameters
        if (normalized.contains("add") && (normalized.contains("item") || normalized.contains("food"))) {
            result.setCommandType(VoiceCommand.CommandType.ADD_ITEM);
            result.setIntent("add_item");
            result.setParameters(extractItemName(normalized));
            result.setResponseText("I'll add that item to your inventory.");
            result.setIsSuccessful(true);
        }
        else if (normalized.contains("expir") || normalized.contains("expire")) {
            result.setCommandType(VoiceCommand.CommandType.CHECK_EXPIRATION);
            result.setIntent("check_expiration");
            result.setResponseText("Let me check which items are expiring soon.");
            result.setIsSuccessful(true);
        }
        else if (normalized.contains("recipe") || normalized.contains("cook") || normalized.contains("make")) {
            result.setCommandType(VoiceCommand.CommandType.GET_RECIPE);
            result.setIntent("get_recipe");
            result.setParameters(extractRecipeQuery(normalized));
            result.setResponseText("I found some recipes for you.");
            result.setIsSuccessful(true);
        }
        else if (normalized.contains("shopping") && normalized.contains("list")) {
            result.setCommandType(VoiceCommand.CommandType.CREATE_SHOPPING_LIST);
            result.setIntent("create_shopping_list");
            result.setResponseText("I'll create a shopping list for you.");
            result.setIsSuccessful(true);
        }
        else if (normalized.contains("log") && (normalized.contains("meal") || normalized.contains("ate") || normalized.contains("eat"))) {
            result.setCommandType(VoiceCommand.CommandType.LOG_MEAL);
            result.setIntent("log_meal");
            result.setParameters(extractMealInfo(normalized));
            result.setResponseText("I've logged your meal.");
            result.setIsSuccessful(true);
        }
        else if (normalized.contains("allergen") || normalized.contains("allergic")) {
            result.setCommandType(VoiceCommand.CommandType.CHECK_ALLERGEN);
            result.setIntent("check_allergen");
            result.setResponseText("Let me check for allergens.");
            result.setIsSuccessful(true);
        }
        else if (normalized.contains("nutrition") || normalized.contains("calories") || normalized.contains("protein")) {
            result.setCommandType(VoiceCommand.CommandType.GET_NUTRITION);
            result.setIntent("get_nutrition");
            result.setResponseText("Here's the nutrition information.");
            result.setIsSuccessful(true);
        }
        else if (normalized.contains("plan") && normalized.contains("meal")) {
            result.setCommandType(VoiceCommand.CommandType.PLAN_MEAL);
            result.setIntent("plan_meal");
            result.setResponseText("I'll help you plan your meals.");
            result.setIsSuccessful(true);
        }
        else {
            result.setCommandType(VoiceCommand.CommandType.OTHER);
            result.setIntent("unknown");
            result.setResponseText("I'm not sure what you want me to do. Can you rephrase that?");
            result.setIsSuccessful(false);
            result.setErrorMessage("Intent not recognized");
        }
        
        long endTime = System.currentTimeMillis();
        result.setProcessingTimeMs((int)(endTime - startTime));
        
        return result;
    }
    
    /**
     * Extract item name from command
     */
    private String extractItemName(String command) {
        // Simple extraction - look for words after "add"
        Pattern pattern = Pattern.compile("add\\s+(?:a\\s+|an\\s+|some\\s+)?([\\w\\s]+?)(?:\\s+to|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
    
    /**
     * Extract recipe query from command
     */
    private String extractRecipeQuery(String command) {
        // Look for food items mentioned
        Pattern pattern = Pattern.compile("(?:recipe|cook|make)\\s+(?:for\\s+|with\\s+)?([\\w\\s]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
    
    /**
     * Extract meal information from command
     */
    private String extractMealInfo(String command) {
        // Extract meal type and food
        Map<String, String> mealInfo = new HashMap<>();
        
        if (command.contains("breakfast")) {
            mealInfo.put("mealType", "BREAKFAST");
        } else if (command.contains("lunch")) {
            mealInfo.put("mealType", "LUNCH");
        } else if (command.contains("dinner")) {
            mealInfo.put("mealType", "DINNER");
        } else if (command.contains("snack")) {
            mealInfo.put("mealType", "SNACK");
        }
        
        return mealInfo.toString();
    }
    
    /**
     * Voice Command Result DTO
     */
    public static class VoiceCommandResult {
        private String commandText;
        private Long userId;
        private VoiceCommand.CommandType commandType;
        private String intent;
        private String parameters;
        private String responseText;
        private Boolean isSuccessful;
        private String errorMessage;
        private Integer processingTimeMs;
        
        // Getters and Setters
        public String getCommandText() {
            return commandText;
        }
        
        public void setCommandText(String commandText) {
            this.commandText = commandText;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public VoiceCommand.CommandType getCommandType() {
            return commandType;
        }
        
        public void setCommandType(VoiceCommand.CommandType commandType) {
            this.commandType = commandType;
        }
        
        public String getIntent() {
            return intent;
        }
        
        public void setIntent(String intent) {
            this.intent = intent;
        }
        
        public String getParameters() {
            return parameters;
        }
        
        public void setParameters(String parameters) {
            this.parameters = parameters;
        }
        
        public String getResponseText() {
            return responseText;
        }
        
        public void setResponseText(String responseText) {
            this.responseText = responseText;
        }
        
        public Boolean getIsSuccessful() {
            return isSuccessful;
        }
        
        public void setIsSuccessful(Boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        public Integer getProcessingTimeMs() {
            return processingTimeMs;
        }
        
        public void setProcessingTimeMs(Integer processingTimeMs) {
            this.processingTimeMs = processingTimeMs;
        }
    }
}
