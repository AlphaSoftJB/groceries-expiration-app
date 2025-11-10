package com.groceriesapp.repository.nutrition;

import com.groceriesapp.model.nutrition.UserDietaryPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserDietaryPreferences entities
 */
@Repository
public interface UserDietaryPreferencesRepository extends JpaRepository<UserDietaryPreferences, Long> {
    
    /**
     * Find dietary preferences for a user
     */
    Optional<UserDietaryPreferences> findByUserId(Long userId);
    
    /**
     * Delete dietary preferences for a user
     */
    void deleteByUserId(Long userId);
}
