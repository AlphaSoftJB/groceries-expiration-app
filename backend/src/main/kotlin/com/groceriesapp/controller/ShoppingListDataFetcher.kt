package com.groceriesapp.controller

import com.groceriesapp.model.ShoppingList
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class ShoppingListDataFetcher {

    @SchemaMapping
    fun createdAt(list: ShoppingList): String {
        return list.createdAt.toString()
    }

    @SchemaMapping
    fun updatedAt(list: ShoppingList): String {
        return list.updatedAt.toString()
    }
}
