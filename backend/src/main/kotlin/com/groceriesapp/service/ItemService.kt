package com.groceriesapp.service

import com.groceriesapp.model.Item
import com.groceriesapp.model.Household
import com.groceriesapp.model.User
import com.groceriesapp.repository.ItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val aiService: AIService,
    private val sustainabilityService: SustainabilityService,
    private val blockchainService: BlockchainService
) {

    @Transactional
    fun createItem(
        name: String,
        quantity: Int,
        expirationDate: LocalDate,
        storageLocation: String?,
        household: Household,
        addedBy: User
    ): Item {
        val predictedExpirationDate = aiService.predictExpirationDate(name, storageLocation, expirationDate)

        val newItem = Item(
            name = name,
            quantity = quantity,
            expirationDate = expirationDate,
            predictedExpirationDate = predictedExpirationDate,
            storageLocation = storageLocation,
            household = household,
            addedBy = addedBy
        )
        return itemRepository.save(newItem)
    }

    fun findItemById(itemId: Long): Item? = itemRepository.findById(itemId).orElse(null)

    fun findAllItemsByHousehold(householdId: Long): List<Item> =
        itemRepository.findAllByHouseholdId(householdId)

    fun findExpiringItems(householdId: Long, daysAhead: Long): List<Item> {
        val dateThreshold = LocalDate.now().plusDays(daysAhead)
        return itemRepository.findAllByHouseholdIdAndExpirationDateBefore(householdId, dateThreshold)
    }

    @Transactional
    fun updateItem(
        item: Item,
        name: String?,
        quantity: Int?,
        expirationDate: LocalDate?,
        storageLocation: String?
    ): Item {
        val newName = name ?: item.name
        val newExpirationDate = expirationDate ?: item.expirationDate
        val newStorageLocation = storageLocation ?: item.storageLocation

        val predictedExpirationDate = aiService.predictExpirationDate(newName, newStorageLocation, newExpirationDate)

        val updatedItem = item.copy(
            name = newName,
            quantity = quantity ?: item.quantity,
            expirationDate = newExpirationDate,
            predictedExpirationDate = predictedExpirationDate,
            storageLocation = newStorageLocation,
            updatedAt = java.time.Instant.now()
        )
        return itemRepository.save(updatedItem)
    }

    @Transactional
    fun deleteItem(itemId: Long) {
        itemRepository.deleteById(itemId)
    }

    @Transactional
    fun markItemAsUsed(itemId: Long, user: User): Item {
        val item = itemRepository.findById(itemId).orElseThrow {
            NoSuchElementException("Item with ID $itemId not found.")
        }

        // 1. Calculate CO2 saved
        val co2Saved = sustainabilityService.calculateCO2Saved(
            itemUsedName = item.name,
            quantity = item.quantity,
            expirationDate = item.expirationDate
        )

        // 2. Update user's sustainability metric
        if (co2Saved > 0) {
            sustainabilityService.updateCO2Saved(user, co2Saved)

            // 3. Check for gamification rewards (Placeholder logic)
            if (user.totalCo2SavedKg + co2Saved >= 10.0 && user.totalCo2SavedKg < 10.0) {
                blockchainService.mintAchievementNFT(user, "First 10kg Saved")
            }
        }

        // 4. Delete the item as it's been used
        itemRepository.delete(item)

        return item // Return the item for confirmation/frontend update
    }
}
