package com.groceriesapp.service;

import com.groceriesapp.model.User;
import com.groceriesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SustainabilityService {
    
    private final UserRepository userRepository;
    
    /**
     * Calculate CO2 saved by using an item before it expires.
     * Formula: (daysBeforeExpiration / 7) * quantity * 0.5 kg CO2 per item
     */
    public double calculateCO2Saved(String itemName, int quantity, LocalDate expirationDate) {
        long daysBeforeExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
        
        if (daysBeforeExpiration < 0) {
            return 0.0; // Item already expired
        }
        
        // Simple formula: more days before expiration = more CO2 saved
        double co2PerItem = 0.5; // kg
        double timeFactor = Math.max(0, daysBeforeExpiration / 7.0);
        
        return timeFactor * quantity * co2PerItem;
    }
    
    @Transactional
    public void updateCO2Saved(User user, double co2Saved) {
        user.setTotalCo2SavedKg(user.getTotalCo2SavedKg() + co2Saved);
        userRepository.save(user);
    }
}
