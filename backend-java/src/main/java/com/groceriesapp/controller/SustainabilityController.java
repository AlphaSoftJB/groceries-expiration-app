package com.groceriesapp.controller;

import com.groceriesapp.model.User;
import com.groceriesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SustainabilityController {
    
    private final UserRepository userRepository;
    
    @QueryMapping
    public Map<String, Object> sustainabilityMetrics() {
        // For demo, get the first user's metrics
        User user = userRepository.findById(1L).orElse(null);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalCo2SavedKg", user != null ? user.getTotalCo2SavedKg() : 0.0);
        
        return metrics;
    }
}
