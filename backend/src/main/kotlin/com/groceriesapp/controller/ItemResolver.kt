package com.groceriesapp.controller

import com.groceriesapp.model.Item
import com.groceriesapp.service.HouseholdService
import com.groceriesapp.service.ItemService
import com.groceriesapp.service.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Controller
class ItemResolver(
    private val itemService: ItemService,
    private val householdService: HouseholdService,
    private val userService: UserService // Used for getting the 'addedBy' user, will be replaced by security context later
) {

    // --- Input Types ---

    data class CreateItemInput(
        val name: String,
        val quantity: Int,
        val expirationDate: String, // YYYY-MM-DD
        val storageLocation: String?,
        val householdId: Long
    )

    data class UpdateItemInput(
        val itemId: Long,
        val name: String?,
        val quantity: Int?,
        val expirationDate: String?, // YYYY-MM-DD
        val storageLocation: String?
    )

    // --- Mutations ---

    @MutationMapping
    fun createItem(@Argument input: CreateItemInput): Item {
        val household = householdService.findById(input.householdId)
            ?: throw NoSuchElementException("Household with ID ${input.householdId} not found.")

        // TODO: Replace with actual authenticated user from security context
        val addedBy = userService.findById(1) // Placeholder user ID 1
            ?: throw IllegalStateException("Placeholder user not found.")

        val expirationDate = try {
            LocalDate.parse(input.expirationDate)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD.")
        }

        return itemService.createItem(
            name = input.name,
            quantity = input.quantity,
            expirationDate = expirationDate,
            storageLocation = input.storageLocation,
            household = household,
            addedBy = addedBy
        )
    }

    @MutationMapping
    fun updateItem(@Argument input: UpdateItemInput): Item {
        val item = itemService.findItemById(input.itemId)
            ?: throw NoSuchElementException("Item with ID ${input.itemId} not found.")

        val expirationDate = input.expirationDate?.let {
            try {
                LocalDate.parse(it)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD.")
            }
        }

        return itemService.updateItem(
            item = item,
            name = input.name,
            quantity = input.quantity,
            expirationDate = expirationDate,
            storageLocation = input.storageLocation
        )
    }

    @MutationMapping
    fun deleteItem(@Argument itemId: Long): Boolean {
        itemService.deleteItem(itemId)
        return true
    }

    @MutationMapping
    fun markItemAsUsed(@Argument itemId: Long): Item {
        // TODO: Replace with actual authenticated user from security context
        val user = userService.findById(1) ?: throw IllegalStateException("Placeholder user not found.")
        return itemService.markItemAsUsed(itemId, user)
    }

    // --- Queries ---

    @QueryMapping
    fun item(@Argument id: Long): Item? {
        return itemService.findItemById(id)
    }

    @QueryMapping
    fun itemsByHousehold(@Argument householdId: Long): List<Item> {
        // TODO: Add security check to ensure user belongs to this household
        return itemService.findAllItemsByHousehold(householdId)
    }

    @QueryMapping
    fun expiringItems(@Argument householdId: Long, @Argument daysAhead: Long): List<Item> {
        // TODO: Add security check to ensure user belongs to this household
        return itemService.findExpiringItems(householdId, daysAhead)
    }
}
