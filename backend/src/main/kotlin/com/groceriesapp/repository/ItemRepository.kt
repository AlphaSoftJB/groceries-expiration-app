package com.groceriesapp.repository

import com.groceriesapp.model.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, Long> {
    fun findAllByHouseholdId(householdId: Long): List<Item>
    fun findAllByHouseholdIdAndExpirationDateBefore(householdId: Long, date: java.time.LocalDate): List<Item>
}
