package com.groceriesapp.controller

import com.groceriesapp.model.User
import com.groceriesapp.model.Household
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class UserDataFetcher {

    @SchemaMapping
    fun createdAt(user: User): String {
        return user.createdAt.toString()
    }

    @SchemaMapping
    fun updatedAt(user: User): String {
        return user.updatedAt.toString()
    }

    @SchemaMapping
    fun household(user: User): Household? {
        return user.household
    }
}
