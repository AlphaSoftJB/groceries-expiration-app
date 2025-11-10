package com.groceriesapp.service;

import com.groceriesapp.model.Household;
import com.groceriesapp.repository.HouseholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseholdService {
    
    private final HouseholdRepository householdRepository;
    
    public List<Household> getAllHouseholds() {
        return householdRepository.findAll();
    }
    
    public Household getHouseholdById(Long id) {
        return householdRepository.findById(id).orElse(null);
    }
}
