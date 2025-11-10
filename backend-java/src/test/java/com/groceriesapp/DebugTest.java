package com.groceriesapp;

import com.groceriesapp.service.EnhancedAIService;
import java.time.LocalDate;

public class DebugTest {
    public static void main(String[] args) {
        EnhancedAIService service = new EnhancedAIService();
        LocalDate predicted = service.predictExpirationDate("milk", "Freezer", null);
        long days = LocalDate.now().until(predicted).getDays();
        System.out.println("Milk in freezer - Days until expiration: " + days);
        System.out.println("Predicted date: " + predicted);
        System.out.println("Today: " + LocalDate.now());
    }
}
