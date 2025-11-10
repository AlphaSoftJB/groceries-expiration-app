package com.groceriesapp.service

import com.groceriesapp.model.Household
import com.groceriesapp.model.ShoppingList
import com.groceriesapp.model.ShoppingListItem
import com.groceriesapp.repository.ShoppingListRepository
import com.groceriesapp.repository.ShoppingListItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShoppingListService(
    private val shoppingListRepository: ShoppingListRepository,
    private val shoppingListItemRepository: ShoppingListItemRepository
) {

    @Transactional
    fun getOrCreateShoppingList(household: Household): ShoppingList {
        return shoppingListRepository.findByHouseholdId(household.id) ?: createShoppingList(household)
    }

    @Transactional
    fun createShoppingList(household: Household): ShoppingList {
        val newList = ShoppingList(name = "Main Shopping List", household = household)
        return shoppingListRepository.save(newList)
    }

    @Transactional
    fun addItemToList(list: ShoppingList, name: String, quantity: Int): ShoppingListItem {
        val newItem = ShoppingListItem(name = name, quantity = quantity, shoppingList = list)
        list.items.add(newItem)
        return shoppingListItemRepository.save(newItem)
    }

    @Transactional
    fun togglePurchased(itemId: Long): ShoppingListItem {
        val item = shoppingListItemRepository.findById(itemId).orElseThrow {
            NoSuchElementException("Shopping list item with ID $itemId not found.")
        }
        val updatedItem = item.copy(isPurchased = !item.isPurchased)
        return shoppingListItemRepository.save(updatedItem)
    }

    /**
     * Simulates a smart suggestion logic based on low stock/consumed items.
     * In a real app, this would analyze consumption history and current inventory.
     */
    fun getSmartSuggestions(household: Household): List<String> {
        // Placeholder logic: Suggest common items that might be low
        val currentItems = listOf("Milk", "Eggs", "Bread")
        val suggestions = mutableListOf<String>()

        if (!currentItems.contains("Milk")) suggestions.add("Milk")
        if (!currentItems.contains("Eggs")) suggestions.add("Eggs")
        if (!currentItems.contains("Bread")) suggestions.add("Bread")
        if (suggestions.isEmpty()) suggestions.add("Coffee")

        return suggestions
    }
}
