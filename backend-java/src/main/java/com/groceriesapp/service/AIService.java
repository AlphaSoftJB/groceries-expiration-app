package com.groceriesapp.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AIService {
    
    /**
     * Placeholder for AI-powered predictive expiration date calculation.
     * In a real implementation, this would call an external ML model or API.
     */
    public LocalDate predictExpirationDate(String itemName, String storageLocation, LocalDate givenExpirationDate) {
        // Simulation: Add 2-5 days to the given expiration date based on storage
        int additionalDays = "Fridge".equalsIgnoreCase(storageLocation) ? 3 : 2;
        return givenExpirationDate.plusDays(additionalDays);
    }
}
