package com.groceriesapp.repository.nutrition;

import com.groceriesapp.model.nutrition.AllergenType;
import com.groceriesapp.model.nutrition.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Ingredient entities
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    /**
     * Find ingredient by name (case-insensitive)
     */
    Optional<Ingredient> findByNameIgnoreCase(String name);
    
    /**
     * Find all allergens
     */
    List<Ingredient> findByIsAllergenTrue();
    
    /**
     * Find ingredients by allergen type
     */
    List<Ingredient> findByAllergenType(AllergenType allergenType);
    
    /**
     * Search ingredients by name (partial match)
     */
    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Ingredient> searchByName(@Param("searchTerm") String searchTerm);
    
    /**
     * Find vegan ingredients
     */
    List<Ingredient> findByIsVeganTrue();
    
    /**
     * Find gluten-free ingredients
     */
    List<Ingredient> findByIsGlutenFreeTrue();
}
