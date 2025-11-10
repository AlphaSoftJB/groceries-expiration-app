package com.groceriesapp.controller

import com.groceriesapp.model.Item
import com.groceriesapp.model.Household
import com.groceriesapp.model.User
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class ItemDataFetcher {

    @SchemaMapping
        @SchemaMapping
    fun predictedExpirationDate(item: Item): String? {
        return item.predictedExpirationDate?.toString()
    }

    @SchemaMapping
    fun expirationDate(item: Item): String {
        return item.expirationDate.toString()
    }

    @SchemaMapping
    fun createdAt(item: Item): String {
        return item.createdAt.toString()
    }

    @SchemaMapping
    fun updatedAt(item: Item): String {
        return item.updatedAt.toString()
    }

    @SchemaMapping
    fun household(item: Item): Household {
        return item.household
    }

    @SchemaMapping
    fun addedBy(item: Item): User {
        return item.addedBy
    }
}
