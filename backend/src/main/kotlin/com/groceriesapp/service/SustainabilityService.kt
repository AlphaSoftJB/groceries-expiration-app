package com.groceriesapp.service

import com.groceriesapp.model.User
import com.groceriesapp.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class SustainabilityService(
    private val userRepository: UserRepository
) {

    // Placeholder map for CO2 equivalent per item type (in kg CO2e per unit)
    // Source: Hypothetical data based on general food waste impact
    private val CO2_EQUIVALENT_PER_ITEM = mapOf(
        "Milk" to 0.5, // kg CO2e per liter/gallon
        "Bread" to 0.3, // kg CO2e per loaf
        "Yogurt" to 0.2, // kg CO2e per container
        "Eggs" to 0.1, // kg CO2e per egg
        "Unknown" to 0.4 // Default value
    )

    /**
     * Calculates the CO2 saved when an item is used before its expiration date.
     *
     * @param itemUsedName The name of the item used.
     * @param quantity The quantity used.
     * @param expirationDate The item's expiration date.
     * @return The calculated CO2 saved in kg.
     */
    fun calculateCO2Saved(itemUsedName: String, quantity: Int, expirationDate: LocalDate): Double {
        val daysUntilExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate)

        // Only count as "saved" if used before expiration
        if (daysUntilExpiration < 0) {
            return 0.0
        }

        // Simple heuristic: CO2e * quantity * (1 - (daysUntilExpiration / totalShelfLife))
        // For simplicity, we'll just use CO2e * quantity if used before expiration.
        val co2ePerUnit = CO2_EQUIVALENT_PER_ITEM.entries
            .firstOrNull { itemUsedName.contains(it.key, ignoreCase = true) }
            ?.value ?: CO2_EQUIVALENT_PER_ITEM["Unknown"]!!

        return co2ePerUnit * quantity
    }

    /**
     * Updates the user's total CO2 saved metric.
     *
     * @param user The user whose metric to update.
     * @param co2Saved The amount of CO2 saved in kg.
     */
    @Transactional
    fun updateCO2Saved(user: User, co2Saved: Double) {
        val updatedUser = user.copy(
            totalCo2SavedKg = user.totalCo2SavedKg + co2Saved
        )
        userRepository.save(updatedUser)
    }
}
