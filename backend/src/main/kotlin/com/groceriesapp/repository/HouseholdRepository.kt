package com.groceriesapp.repository

import com.groceriesapp.model.Household
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HouseholdRepository : JpaRepository<Household, Long>
