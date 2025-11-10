package com.groceriesapp.controller

import com.groceriesapp.model.Household
import com.groceriesapp.model.ShoppingList
import com.groceriesapp.model.ShoppingListItem
import com.groceriesapp.service.HouseholdService
import com.groceriesapp.service.ShoppingListService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class ShoppingListResolver(
    private val shoppingListService: ShoppingListService,
    private val householdService: HouseholdService
) {

    // --- Input Types ---
    data class AddItemToShoppingListInput(
        val listId: Long,
        val name: String,
        val quantity: Int
    )

    // --- Queries ---

    @QueryMapping
    fun shoppingListByHousehold(@Argument householdId: Long): ShoppingList {
        val household = householdService.findById(householdId)
            ?: throw NoSuchElementException("Household with ID $householdId not found.")
        return shoppingListService.getOrCreateShoppingList(household)
    }

    @QueryMapping
    fun smartShoppingSuggestions(@Argument householdId: Long): List<String> {
        val household = householdService.findById(householdId)
            ?: throw NoSuchElementException("Household with ID $householdId not found.")
        return shoppingListService.getSmartSuggestions(household)
    }

    // --- Mutations ---

    @MutationMapping
    fun addItemToShoppingList(@Argument input: AddItemToShoppingListInput): ShoppingListItem {
        val list = shoppingListService.getOrCreateShoppingList(
            householdService.findById(1)!! // Placeholder household
        )
        return shoppingListService.addItemToList(list, input.name, input.quantity)
    }

    @MutationMapping
    fun toggleShoppingListItem(@Argument itemId: Long): ShoppingListItem {
        return shoppingListService.togglePurchased(itemId)
    }

    // --- Data Fetchers for Nested Fields ---

    @SchemaMapping
    fun items(shoppingList: ShoppingList): List<ShoppingListItem> {
        return shoppingList.items.toList()
    }
}
