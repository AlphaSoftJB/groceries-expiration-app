package com.groceriesapp.controller

import com.groceriesapp.model.User
import com.groceriesapp.service.UserService
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

data class SustainabilityMetrics(
    val totalCo2SavedKg: Double
)

@Controller
class SustainabilityResolver(
    private val userService: UserService
) {

    @QueryMapping
    fun sustainabilityMetrics(): SustainabilityMetrics {
        // TODO: Replace with actual authenticated user from security context
        val user = userService.findById(1) ?: throw IllegalStateException("Placeholder user not found.")

        return SustainabilityMetrics(
            totalCo2SavedKg = user.totalCo2SavedKg
        )
    }

    @MutationMapping
    fun markItemAsUsed(itemId: Long): Boolean {
        // This mutation is handled by ItemResolver, but we need to ensure the QueryMapping is here
        // to expose the metrics.
        return true
    }
}
