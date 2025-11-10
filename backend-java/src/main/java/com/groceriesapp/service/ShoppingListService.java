package com.groceriesapp.service;

import com.groceriesapp.model.Household;
import com.groceriesapp.model.ShoppingList;
import com.groceriesapp.model.ShoppingListItem;
import com.groceriesapp.repository.HouseholdRepository;
import com.groceriesapp.repository.ShoppingListItemRepository;
import com.groceriesapp.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingListService {
    
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;
    private final HouseholdRepository householdRepository;
    
    @Transactional
    public ShoppingList getOrCreateShoppingList(Long householdId) {
        return shoppingListRepository.findByHouseholdId(householdId)
            .orElseGet(() -> {
                Household household = householdRepository.findById(householdId)
                    .orElseThrow(() -> new RuntimeException("Household not found"));
                
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setName("Default Shopping List");
                shoppingList.setHousehold(household);
                return shoppingListRepository.save(shoppingList);
            });
    }
    
    @Transactional
    public ShoppingListItem addItemToList(Long listId, String name, Integer quantity) {
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("Shopping list not found"));
        
        ShoppingListItem item = new ShoppingListItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setIsPurchased(false);
        item.setShoppingList(shoppingList);
        
        return shoppingListItemRepository.save(item);
    }
    
    @Transactional
    public ShoppingListItem toggleItemPurchased(Long itemId) {
        ShoppingListItem item = shoppingListItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Shopping list item not found"));
        
        item.setIsPurchased(!item.getIsPurchased());
        return shoppingListItemRepository.save(item);
    }
    
    /**
     * Placeholder for smart shopping suggestions based on consumption patterns.
     */
    public List<String> getSmartSuggestions(Long householdId) {
        // In a real implementation, this would analyze consumption history
        return Arrays.asList("Milk", "Eggs", "Bread", "Apples", "Chicken");
    }
}
