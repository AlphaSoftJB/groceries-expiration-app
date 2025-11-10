package com.groceriesapp.service;

import com.groceriesapp.model.Household;
import com.groceriesapp.model.User;
import com.groceriesapp.repository.HouseholdRepository;
import com.groceriesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final HouseholdRepository householdRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public User createUser(String email, String password, String name, String householdName) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        
        // Create or find household
        if (householdName != null && !householdName.isEmpty()) {
            Household household = new Household();
            household.setName(householdName);
            household = householdRepository.save(household);
            user.setHousehold(household);
        }
        
        return userRepository.save(user);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
