package com.groceriesapp.repository

import com.groceriesapp.model.ShoppingList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShoppingListRepository : JpaRepository<ShoppingList, Long> {
    fun findByHouseholdId(householdId: Long): ShoppingList?
}
