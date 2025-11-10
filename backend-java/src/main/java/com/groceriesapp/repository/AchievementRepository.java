package com.groceriesapp.repository;

import com.groceriesapp.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    
    List<Achievement> findByIsActiveTrue();
    
    Optional<Achievement> findByType(String type);
    
    List<Achievement> findByTier(String tier);
}
