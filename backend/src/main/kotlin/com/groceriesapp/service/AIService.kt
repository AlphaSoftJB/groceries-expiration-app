package com.groceriesapp.service

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class AIService {

    /**
     * Simulates a call to an external AI/ML model to predict the expiration date.
     * In a real application, this would involve an HTTP call to a dedicated AI inference service.
     *
     * @param itemName The name of the item.
     * @param storageLocation The storage location (e.g., "Fridge", "Pantry").
     * @param declaredExpirationDate The expiration date declared on the packaging.
     * @return A predicted expiration date (LocalDate) or null if prediction is unavailable.
     */
    fun predictExpirationDate(itemName: String, storageLocation: String?, declaredExpirationDate: LocalDate): LocalDate? {
        // Placeholder logic:
        // 1. If the item is "Milk" and in the "Fridge", predict 3 days after the declared date.
        // 2. If the item is "Bread" and in the "Pantry", predict 2 days before the declared date.
        // 3. Otherwise, return null (no prediction).

        return when {
            itemName.equals("Milk", ignoreCase = true) && storageLocation.equals("Fridge", ignoreCase = true) -> {
                declaredExpirationDate.plus(3, ChronoUnit.DAYS)
            }
            itemName.equals("Bread", ignoreCase = true) && storageLocation.equals("Pantry", ignoreCase = true) -> {
                declaredExpirationDate.minus(2, ChronoUnit.DAYS)
            }
            else -> null
        }
    }
}
