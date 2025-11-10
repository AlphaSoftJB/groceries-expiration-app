package com.groceriesapp.repository;

import com.groceriesapp.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByHouseholdId(Long householdId);
    List<Item> findByExpirationDateBefore(LocalDate date);
    List<Item> findByHouseholdIdAndExpirationDateBefore(Long householdId, LocalDate date);
}
