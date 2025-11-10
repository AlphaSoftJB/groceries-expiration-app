package com.groceriesapp.controller

import com.groceriesapp.model.Household
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class HouseholdDataFetcher {

    @SchemaMapping
    fun createdAt(household: Household): String {
        return household.createdAt.toString()
    }

    @SchemaMapping
    fun updatedAt(household: Household): String {
        return household.updatedAt.toString()
    }
}
