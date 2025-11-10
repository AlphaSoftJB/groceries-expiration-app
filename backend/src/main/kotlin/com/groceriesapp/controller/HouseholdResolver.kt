package com.groceriesapp.controller

import com.groceriesapp.model.Household
import com.groceriesapp.service.HouseholdService
import org.springframework.graphql.data.method.annotation.QueryMapping
import com.groceriesapp.model.Household
import com.groceriesapp.model.User
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class HouseholdResolver(
    private val householdService: HouseholdService
) {
    @QueryMapping
    fun allHouseholds(): List<Household> {
        return householdService.findAll()
    }
}
