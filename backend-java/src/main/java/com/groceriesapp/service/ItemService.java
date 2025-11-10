package com.groceriesapp.service;

import com.groceriesapp.model.Household;
import com.groceriesapp.model.Item;
import com.groceriesapp.model.User;
import com.groceriesapp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final AIService aiService;
    private final EnhancedAIService enhancedAIService;
    private final SustainabilityService sustainabilityService;
    
    @Transactional
    public Item createItem(String name, Integer quantity, LocalDate expirationDate, 
                          String storageLocation, Household household, User addedBy) {
        Item item = new Item();
        item.setName(name);
        item.setQuantity(quantity);
        item.setExpirationDate(expirationDate);
        item.setStorageLocation(storageLocation);
        item.setHousehold(household);
        item.setAddedBy(addedBy);
        
        // Call AI service to predict expiration date
        LocalDate predictedDate = aiService.predictExpirationDate(name, storageLocation, expirationDate);
        item.setPredictedExpirationDate(predictedDate);
        
        return itemRepository.save(item);
    }
    
    @Transactional
    public Item updateItem(Long itemId, String name, Integer quantity, 
                          LocalDate expirationDate, String storageLocation) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (name != null) item.setName(name);
        if (quantity != null) item.setQuantity(quantity);
        if (expirationDate != null) {
            item.setExpirationDate(expirationDate);
            // Re-predict expiration date if the date changes
            LocalDate predictedDate = aiService.predictExpirationDate(
                item.getName(), item.getStorageLocation(), expirationDate);
            item.setPredictedExpirationDate(predictedDate);
        }
        if (storageLocation != null) item.setStorageLocation(storageLocation);
        
        return itemRepository.save(item);
    }
    
    @Transactional
    public boolean deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            return false;
        }
        itemRepository.deleteById(itemId);
        return true;
    }
    
    @Transactional
    public Item markItemAsUsed(Long itemId, User user) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        // Calculate CO2 saved
        double co2Saved = sustainabilityService.calculateCO2Saved(
            item.getName(), item.getQuantity(), item.getExpirationDate());
        
        // Update user's total CO2 saved
        sustainabilityService.updateCO2Saved(user, co2Saved);
        
        // Delete the item (it's been used)
        itemRepository.delete(item);
        
        return item;
    }
    
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }
    
    public List<Item> getItemsByHousehold(Long householdId) {
        return itemRepository.findByHouseholdId(householdId);
    }
    
    public List<Item> getExpiringItems(Long householdId, int daysAhead) {
        LocalDate targetDate = LocalDate.now().plusDays(daysAhead);
        return itemRepository.findByHouseholdIdAndExpirationDateBefore(householdId, targetDate);
    }
}
