package com.groceriesapp.service

import com.groceriesapp.model.Household
import com.groceriesapp.model.User
import org.springframework.stereotype.Service
import java.time.LocalDate

data class ApplianceItemData(
    val name: String,
    val quantity: Int,
    val expirationDate: LocalDate?
)

@Service
class SmartApplianceService(
    private val itemService: ItemService
) {

    /**
     * Simulates syncing data from a smart appliance (e.g., smart fridge camera/scanner).
     * In a real application, this would involve a complex integration with a vendor API.
     *
     * @param household The household to sync data to.
     * @param user The user initiating the sync.
     * @param applianceId The ID of the appliance.
     * @param rawData A list of item data received from the appliance.
     * @return A summary of the sync operation.
     */
    fun syncApplianceData(household: Household, user: User, applianceId: String, rawData: List<ApplianceItemData>): String {
        var itemsAdded = 0
        var itemsUpdated = 0

        for (data in rawData) {
            // Placeholder logic: Check if item already exists (simplified)
            val existingItem = itemService.findAllItemsByHousehold(household.id)
                .firstOrNull { it.name.equals(data.name, ignoreCase = true) }

            if (existingItem != null) {
                // Simulate update logic (e.g., update quantity, check for new expiration date)
                itemService.updateItem(
                    item = existingItem,
                    name = data.name,
                    quantity = data.quantity,
                    expirationDate = data.expirationDate,
                    storageLocation = "Smart Fridge - $applianceId"
                )
                itemsUpdated++
            } else {
                // Simulate creation logic
                if (data.expirationDate != null) {
                    itemService.createItem(
                        name = data.name,
                        quantity = data.quantity,
                        expirationDate = data.expirationDate,
                        storageLocation = "Smart Fridge - $applianceId",
                        household = household,
                        addedBy = user
                    )
                    itemsAdded++
                }
            }
        }

        return "Sync complete for appliance $applianceId. Added $itemsAdded items, updated $itemsUpdated items."
    }
}
