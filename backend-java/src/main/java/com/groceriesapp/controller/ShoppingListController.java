package com.groceriesapp.controller;

import com.groceriesapp.model.ShoppingList;
import com.groceriesapp.model.ShoppingListItem;
import com.groceriesapp.service.ShoppingListService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ShoppingListController {
    
    private final ShoppingListService shoppingListService;
    
    @QueryMapping
    public ShoppingList shoppingListByHousehold(@Argument Long householdId) {
        return shoppingListService.getOrCreateShoppingList(householdId);
    }
    
    @QueryMapping
    public List<String> smartShoppingSuggestions(@Argument Long householdId) {
        return shoppingListService.getSmartSuggestions(householdId);
    }
    
    @MutationMapping
    public ShoppingListItem addItemToShoppingList(@Argument Map<String, Object> input) {
        Long listId = Long.parseLong(input.get("listId").toString());
        String name = (String) input.get("name");
        Integer quantity = (Integer) input.get("quantity");
        
        return shoppingListService.addItemToList(listId, name, quantity);
    }
    
    @MutationMapping
    public ShoppingListItem toggleShoppingListItem(@Argument Long itemId) {
        return shoppingListService.toggleItemPurchased(itemId);
    }
}
