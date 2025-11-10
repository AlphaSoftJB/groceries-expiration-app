package com.groceriesapp.repository

import com.groceriesapp.model.ShoppingListItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShoppingListItemRepository : JpaRepository<ShoppingListItem, Long>
