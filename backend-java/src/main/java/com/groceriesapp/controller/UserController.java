package com.groceriesapp.controller;

import com.groceriesapp.model.Household;
import com.groceriesapp.model.User;
import com.groceriesapp.service.HouseholdService;
import com.groceriesapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final HouseholdService householdService;
    
    @QueryMapping
    public String hello() {
        return "Hello from Groceries Expiration Tracker API!";
    }
    
    @QueryMapping
    public User user(@Argument Long id) {
        return userService.getUserById(id);
    }
    
    @QueryMapping
    public List<Household> allHouseholds() {
        return householdService.getAllHouseholds();
    }
    
    @MutationMapping
    public User createUser(@Argument Map<String, Object> input) {
        String email = (String) input.get("email");
        String password = (String) input.get("password");
        String name = (String) input.get("name");
        String householdName = (String) input.get("householdName");
        
        return userService.createUser(email, password, name, householdName);
    }
}
