package com.groceriesapp.controller

import com.groceriesapp.GroceriesAppApplication
import com.groceriesapp.model.Household
import com.groceriesapp.model.User
import com.groceriesapp.repository.HouseholdRepository
import com.groceriesapp.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.HttpGraphQlTester
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [GroceriesAppApplication::class])
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
@Transactional
class ItemResolverTest {

    @Autowired
    private lateinit var graphQlTester: HttpGraphQlTester

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var householdRepository: HouseholdRepository

    private lateinit var testHousehold: Household
    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        // Setup test data
        testHousehold = householdRepository.save(Household(name = "Test Household"))
        testUser = userRepository.save(User(email = "test@user.com", password = "hashed", household = testHousehold))
    }

    @Test
    fun `itemsByHousehold query should return a list of items`() {
        // Create an item via mutation (assuming ItemResolver is functional)
        val mutation = """
            mutation CreateItem(\$input: CreateItemInput!) {
                createItem(input: \$input) {
                    id
                    name
                }
            }
        """.trimIndent()

        val input = mapOf(
            "name" to "Test Item",
            "quantity" to 5,
            "expirationDate" to "2025-12-31",
            "householdId" to testHousehold.id.toString()
        )

        graphQlTester.document(mutation)
            .variable("input", input)
            .execute()
            .path("createItem.id").entity(String::class.java).isNotNull
            .path("createItem.name").isEqualTo("Test Item")

        // Query the item
        val query = """
            query ItemsQuery(\$householdId: ID!) {
                itemsByHousehold(householdId: \$householdId) {
                    name
                    quantity
                }
            }
        """.trimIndent()

        graphQlTester.document(query)
            .variable("householdId", testHousehold.id.toString())
            .execute()
            .path("itemsByHousehold").entityList(Map::class.java).hasSize(1)
            .path("itemsByHousehold[0].name").isEqualTo("Test Item")
            .path("itemsByHousehold[0].quantity").isEqualTo(5)
    }
}
