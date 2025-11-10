package com.groceriesapp.repository.nutrition;

import com.groceriesapp.model.nutrition.AllergenType;
import com.groceriesapp.model.nutrition.UserAllergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserAllergen entities
 */
@Repository
public interface UserAllergenRepository extends JpaRepository<UserAllergen, Long> {
    
    /**
     * Find all allergens for a user
     */
    List<UserAllergen> findByUserId(Long userId);
    
    /**
     * Find specific allergen for a user
     */
    Optional<UserAllergen> findByUserIdAndAllergenType(Long userId, AllergenType allergenType);
    
    /**
     * Delete allergen for a user
     */
    void deleteByUserIdAndAllergenType(Long userId, AllergenType allergenType);
    
    /**
     * Find severe allergens for a user (severity = SEVERE or LIFE_THREATENING)
     */
    @org.springframework.data.jpa.repository.Query("SELECT ua FROM UserAllergen ua WHERE ua.userId = ?1 AND (ua.severity = 'SEVERE' OR ua.severity = 'LIFE_THREATENING')")
    List<UserAllergen> findSevereAllergens(Long userId);
}
