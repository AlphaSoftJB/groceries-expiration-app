package com.groceriesapp.repository.nutrition;

import com.groceriesapp.model.nutrition.ItemIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ItemIngredient entities
 */
@Repository
public interface ItemIngredientRepository extends JpaRepository<ItemIngredient, Long> {
    
    /**
     * Find all ingredients for an item
     */
    List<ItemIngredient> findByItemIdOrderByPositionAsc(Long itemId);
    
    /**
     * Delete all ingredients for an item
     */
    void deleteByItemId(Long itemId);
}
