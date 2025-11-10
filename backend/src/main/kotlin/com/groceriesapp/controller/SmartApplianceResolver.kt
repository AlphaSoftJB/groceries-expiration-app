package com.groceriesapp.controller

import com.groceriesapp.service.ApplianceItemData
import com.groceriesapp.service.HouseholdService
import com.groceriesapp.service.SmartApplianceService
import com.groceriesapp.service.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Controller
class SmartApplianceResolver(
    private val smartApplianceService: SmartApplianceService,
    private val householdService: HouseholdService,
    private val userService: UserService
) {

    data class ApplianceItemInput(
        val name: String,
        val quantity: Int,
        val expirationDate: String? // YYYY-MM-DD
    )

    data class SyncApplianceInput(
        val applianceId: String,
        val householdId: Long,
        val items: List<ApplianceItemInput>
    )

    @MutationMapping
    fun syncApplianceData(@Argument input: SyncApplianceInput): String {
        // TODO: Replace with actual authenticated user from security context
        val user = userService.findById(1) ?: throw IllegalStateException("Placeholder user not found.")
        val household = householdService.findById(input.householdId)
            ?: throw NoSuchElementException("Household with ID ${input.householdId} not found.")

        val applianceData = input.items.map {
            val expirationDate = it.expirationDate?.let { dateString ->
                try {
                    LocalDate.parse(dateString)
                } catch (e: DateTimeParseException) {
                    throw IllegalArgumentException("Invalid date format for item ${it.name}. Expected YYYY-MM-DD.")
                }
            }
            ApplianceItemData(it.name, it.quantity, expirationDate)
        }

        return smartApplianceService.syncApplianceData(
            household = household,
            user = user,
            applianceId = input.applianceId,
            rawData = applianceData
        )
    }
}
