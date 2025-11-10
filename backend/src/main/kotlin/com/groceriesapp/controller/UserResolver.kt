package com.groceriesapp.controller

import com.groceriesapp.model.User
import com.groceriesapp.service.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserResolver(
    private val userService: UserService
) {

    data class CreateUserInput(
        val email: String,
        val password: String,
        val name: String,
        val householdName: String?
    )

    @MutationMapping
    fun createUser(@Argument input: CreateUserInput): User {
        return userService.createUser(
            email = input.email,
            password = input.password,
            name = input.name,
            householdName = input.householdName
        )
    }

    @QueryMapping
    fun user(@Argument id: Long): User? {
        return userService.findById(id)
    }

    // This will be implemented later with security context
    @QueryMapping
    fun me(): User? {
        return null
    }
}
