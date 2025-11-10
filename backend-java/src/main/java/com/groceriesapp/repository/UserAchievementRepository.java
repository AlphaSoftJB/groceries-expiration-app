package com.groceriesapp.repository;

import com.groceriesapp.model.User;
import com.groceriesapp.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    
    List<UserAchievement> findByUser(User user);
    
    List<UserAchievement> findByUserAndIsUnlockedTrue(User user);
    
    Optional<UserAchievement> findByUserAndAchievement_Type(User user, String achievementType);
}
