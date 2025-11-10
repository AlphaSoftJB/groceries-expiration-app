package com.groceriesapp.controller;

import com.groceriesapp.model.Household;
import com.groceriesapp.model.Item;
import com.groceriesapp.model.User;
import com.groceriesapp.repository.HouseholdRepository;
import com.groceriesapp.repository.UserRepository;
import com.groceriesapp.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ItemController {
    
    private final ItemService itemService;
    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;
    
    @QueryMapping
    public Item item(@Argument Long id) {
        return itemService.getItemById(id);
    }
    
    @QueryMapping
    public List<Item> itemsByHousehold(@Argument Long householdId) {
        return itemService.getItemsByHousehold(householdId);
    }
    
    @QueryMapping
    public List<Item> expiringItems(@Argument Long householdId, @Argument int daysAhead) {
        return itemService.getExpiringItems(householdId, daysAhead);
    }
    
    @MutationMapping
    public Item createItem(@Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        Integer quantity = (Integer) input.get("quantity");
        String expirationDateStr = (String) input.get("expirationDate");
        String storageLocation = (String) input.get("storageLocation");
        Long householdId = Long.parseLong(input.get("householdId").toString());
        
        LocalDate expirationDate = LocalDate.parse(expirationDateStr);
        
        Household household = householdRepository.findById(householdId)
            .orElseThrow(() -> new RuntimeException("Household not found"));
        
        // For demo, use the first user in the household
        User addedBy = userRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return itemService.createItem(name, quantity, expirationDate, storageLocation, household, addedBy);
    }
    
    @MutationMapping
    public Item updateItem(@Argument Map<String, Object> input) {
        Long itemId = Long.parseLong(input.get("itemId").toString());
        String name = (String) input.get("name");
        Integer quantity = (Integer) input.get("quantity");
        String expirationDateStr = (String) input.get("expirationDate");
        String storageLocation = (String) input.get("storageLocation");
        
        LocalDate expirationDate = expirationDateStr != null ? LocalDate.parse(expirationDateStr) : null;
        
        return itemService.updateItem(itemId, name, quantity, expirationDate, storageLocation);
    }
    
    @MutationMapping
    public Boolean deleteItem(@Argument Long itemId) {
        return itemService.deleteItem(itemId);
    }
    
    @MutationMapping
    public Item markItemAsUsed(@Argument Long itemId) {
        // For demo, use the first user
        User user = userRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return itemService.markItemAsUsed(itemId, user);
    }
}
