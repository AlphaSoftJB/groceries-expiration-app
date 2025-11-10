package com.groceriesapp.service

import com.groceriesapp.model.Household
import com.groceriesapp.model.Item
import com.groceriesapp.model.User
import com.groceriesapp.repository.ItemRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate
import java.util.*

@SpringBootTest
class ItemServiceTest {

    @MockBean
    private lateinit var itemRepository: ItemRepository

    @MockBean
    private lateinit var aiService: AIService

    @MockBean
    private lateinit var sustainabilityService: SustainabilityService

    @MockBean
    private lateinit var blockchainService: BlockchainService

    private lateinit var itemService: ItemService

    private val testHousehold = Household(id = 1, name = "Test Household")
    private val testUser = User(id = 1, email = "test@user.com", password = "hashed", household = testHousehold)

    @BeforeEach
    fun setUp() {
        itemService = ItemService(itemRepository, aiService, sustainabilityService, blockchainService)
    }

    @Test
    fun `createItem should save item with predicted expiration date`() {
        // Arrange
        val name = "Milk"
        val quantity = 1
        val expirationDate = LocalDate.now().plusDays(7)
        val storageLocation = "Fridge"
        val predictedDate = LocalDate.now().plusDays(10)

        `when`(aiService.predictExpirationDate(name, storageLocation, expirationDate)).thenReturn(predictedDate)
        `when`(itemRepository.save(any(Item::class.java))).thenAnswer { it.arguments[0] }

        // Act
        val createdItem = itemService.createItem(name, quantity, expirationDate, storageLocation, testHousehold, testUser)

        // Assert
        assertNotNull(createdItem)
        assertEquals(name, createdItem.name)
        assertEquals(predictedDate, createdItem.predictedExpirationDate)
        verify(itemRepository, times(1)).save(any(Item::class.java))
    }

    @Test
    fun `markItemAsUsed should calculate co2 saved and delete item`() {
        // Arrange
        val itemId = 1L
        val item = Item(
            id = itemId,
            name = "Milk",
            quantity = 1,
            expirationDate = LocalDate.now().plusDays(5),
            household = testHousehold,
            addedBy = testUser
        )

        `when`(itemRepository.findById(itemId)).thenReturn(Optional.of(item))
        `when`(sustainabilityService.calculateCO2Saved(anyString(), anyInt(), any(LocalDate::class.java))).thenReturn(0.5)
        `when`(itemRepository.delete(any(Item::class.java))).then {}

        // Act
        itemService.markItemAsUsed(itemId, testUser)

        // Assert
        verify(sustainabilityService, times(1)).calculateCO2Saved("Milk", 1, item.expirationDate)
        verify(sustainabilityService, times(1)).updateCO2Saved(testUser, 0.5)
        verify(itemRepository, times(1)).delete(item)
    }

    @Test
    fun `markItemAsUsed should throw exception if item not found`() {
        // Arrange
        val itemId = 99L
        `when`(itemRepository.findById(itemId)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(NoSuchElementException::class.java) {
            itemService.markItemAsUsed(itemId, testUser)
        }
    }
}
