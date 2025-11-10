package com.groceriesapp.controller

import com.groceriesapp.model.ShoppingListItem
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class ShoppingListItemDataFetcher {

    @SchemaMapping
    fun createdAt(item: ShoppingListItem): String {
        return item.createdAt.toString()
    }

    @SchemaMapping
    fun updatedAt(item: ShoppingListItem): String {
        return item.updatedAt.toString()
    }
}
