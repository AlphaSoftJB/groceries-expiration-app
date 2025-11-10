package com.groceriesapp.service

import com.groceriesapp.model.Household
import com.groceriesapp.repository.HouseholdRepository
import org.springframework.stereotype.Service

@Service
class HouseholdService(
    private val householdRepository: HouseholdRepository
) {
    fun findAll(): List<Household> = householdRepository.findAll()

    fun findById(id: Long): Household? = householdRepository.findById(id).orElse(null)
}
