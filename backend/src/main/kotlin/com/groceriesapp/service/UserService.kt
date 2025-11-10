package com.groceriesapp.service

import com.groceriesapp.model.Household
import com.groceriesapp.model.User
import com.groceriesapp.repository.HouseholdRepository
import com.groceriesapp.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val householdRepository: HouseholdRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(email: String, password: String, name: String, householdName: String?): User {
        if (userRepository.findByEmail(email) != null) {
            throw IllegalArgumentException("User with email $email already exists.")
        }

        val household = if (householdName != null) {
            householdRepository.save(Household(name = householdName))
        } else {
            null
        }

        val newUser = User(
            email = email,
            passwordHash = passwordEncoder.encode(password),
            name = name,
            household = household
        )

        return userRepository.save(newUser)
    }

    fun findById(id: Long): User? = userRepository.findById(id).orElse(null)

    fun findByEmail(email: String): User? = userRepository.findByEmail(email)
}
