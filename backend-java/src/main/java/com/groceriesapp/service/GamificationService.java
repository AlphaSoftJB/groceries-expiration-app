package com.groceriesapp.service;

import com.groceriesapp.model.Achievement;
import com.groceriesapp.model.User;
import com.groceriesapp.model.UserAchievement;
import com.groceriesapp.repository.AchievementRepository;
import com.groceriesapp.repository.UserAchievementRepository;
import com.groceriesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamificationService {
    
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    
    // XP required for each level (exponential growth)
    private static final int BASE_XP_PER_LEVEL = 100;
    private static final double LEVEL_MULTIPLIER = 1.5;
    
    /**
     * Initialize achievements in the database (call this on startup).
     */
    @Transactional
    public void initializeAchievements() {
        if (achievementRepository.count() > 0) {
            return; // Already initialized
        }
        
        List<Achievement> achievements = new ArrayList<>();
        
        // Waste Warrior Achievements
        achievements.add(createAchievement("Waste Warrior I", "Save 10 items from expiring", 
            "WASTE_WARRIOR", "BRONZE", 10, 50, "🥉"));
        achievements.add(createAchievement("Waste Warrior II", "Save 50 items from expiring", 
            "WASTE_WARRIOR", "SILVER", 50, 150, "🥈"));
        achievements.add(createAchievement("Waste Warrior III", "Save 100 items from expiring", 
            "WASTE_WARRIOR", "GOLD", 100, 300, "🥇"));
        achievements.add(createAchievement("Waste Warrior IV", "Save 500 items from expiring", 
            "WASTE_WARRIOR", "PLATINUM", 500, 1000, "💎"));
        
        // Eco Champion Achievements
        achievements.add(createAchievement("Eco Champion I", "Save 10kg of CO2", 
            "ECO_CHAMPION", "BRONZE", 10, 50, "🌱"));
        achievements.add(createAchievement("Eco Champion II", "Save 50kg of CO2", 
            "ECO_CHAMPION", "SILVER", 50, 150, "🌿"));
        achievements.add(createAchievement("Eco Champion III", "Save 100kg of CO2", 
            "ECO_CHAMPION", "GOLD", 100, 300, "🌳"));
        achievements.add(createAchievement("Eco Champion IV", "Save 500kg of CO2", 
            "ECO_CHAMPION", "PLATINUM", 500, 1000, "🌲"));
        
        // Scan Master Achievements
        achievements.add(createAchievement("Scan Master I", "Scan 10 items", 
            "SCAN_MASTER", "BRONZE", 10, 30, "📸"));
        achievements.add(createAchievement("Scan Master II", "Scan 50 items", 
            "SCAN_MASTER", "SILVER", 50, 100, "📷"));
        achievements.add(createAchievement("Scan Master III", "Scan 200 items", 
            "SCAN_MASTER", "GOLD", 200, 250, "📹"));
        
        // Streak Achievements
        achievements.add(createAchievement("Consistent I", "7-day streak", 
            "STREAK", "BRONZE", 7, 100, "🔥"));
        achievements.add(createAchievement("Consistent II", "30-day streak", 
            "STREAK", "SILVER", 30, 300, "🔥"));
        achievements.add(createAchievement("Consistent III", "100-day streak", 
            "STREAK", "GOLD", 100, 1000, "🔥"));
        
        achievementRepository.saveAll(achievements);
    }
    
    /**
     * Award XP to a user and check for level up.
     */
    @Transactional
    public Map<String, Object> awardExperience(Long userId, Integer xp, String reason) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        int oldLevel = user.getLevel();
        int oldXP = user.getExperiencePoints();
        
        // Add XP
        user.setExperiencePoints(oldXP + xp);
        
        // Check for level up
        int newLevel = calculateLevel(user.getExperiencePoints());
        boolean leveledUp = newLevel > oldLevel;
        
        if (leveledUp) {
            user.setLevel(newLevel);
        }
        
        userRepository.save(user);
        
        // Update streak
        updateStreak(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("oldLevel", oldLevel);
        result.put("newLevel", newLevel);
        result.put("leveledUp", leveledUp);
        result.put("xpGained", xp);
        result.put("totalXP", user.getExperiencePoints());
        result.put("reason", reason);
        
        return result;
    }
    
    /**
     * Track progress towards an achievement.
     */
    @Transactional
    public List<Map<String, Object>> trackAchievementProgress(User user, String achievementType, Integer progress) {
        List<Achievement> achievements = achievementRepository.findByIsActiveTrue().stream()
            .filter(a -> a.getType().equals(achievementType))
            .sorted(Comparator.comparing(Achievement::getPointsRequired))
            .collect(Collectors.toList());
        
        List<Map<String, Object>> unlockedAchievements = new ArrayList<>();
        
        for (Achievement achievement : achievements) {
            UserAchievement userAchievement = userAchievementRepository
                .findByUserAndAchievement_Type(user, achievementType)
                .orElse(new UserAchievement());
            
            if (userAchievement.getId() == null) {
                userAchievement.setUser(user);
                userAchievement.setAchievement(achievement);
            }
            
            userAchievement.setProgress(progress);
            
            // Check if achievement should be unlocked
            if (!userAchievement.getIsUnlocked() && progress >= achievement.getPointsRequired()) {
                userAchievement.setIsUnlocked(true);
                userAchievement.setUnlockedAt(LocalDateTime.now());
                
                // Award XP for unlocking achievement
                awardExperience(user.getId(), achievement.getXpReward(), 
                    "Unlocked: " + achievement.getName());
                
                Map<String, Object> unlocked = new HashMap<>();
                unlocked.put("achievementId", achievement.getId());
                unlocked.put("name", achievement.getName());
                unlocked.put("description", achievement.getDescription());
                unlocked.put("tier", achievement.getTier());
                unlocked.put("badgeIcon", achievement.getBadgeIcon());
                unlocked.put("xpReward", achievement.getXpReward());
                unlockedAchievements.add(unlocked);
            }
            
            userAchievementRepository.save(userAchievement);
        }
        
        return unlockedAchievements;
    }
    
    /**
     * Get user's gamification stats.
     */
    public Map<String, Object> getUserStats(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<UserAchievement> unlockedAchievements = userAchievementRepository
            .findByUserAndIsUnlockedTrue(user);
        
        int xpForNextLevel = calculateXPForLevel(user.getLevel() + 1);
        int xpProgress = user.getExperiencePoints() - calculateXPForLevel(user.getLevel());
        int xpNeeded = xpForNextLevel - calculateXPForLevel(user.getLevel());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("level", user.getLevel());
        stats.put("experiencePoints", user.getExperiencePoints());
        stats.put("xpProgress", xpProgress);
        stats.put("xpNeeded", xpNeeded);
        stats.put("totalPoints", user.getTotalPoints());
        stats.put("itemsSaved", user.getItemsSaved());
        stats.put("itemsScanned", user.getItemsScanned());
        stats.put("streak", user.getStreak());
        stats.put("totalCo2Saved", user.getTotalCo2SavedKg());
        stats.put("achievementsUnlocked", unlockedAchievements.size());
        stats.put("achievements", unlockedAchievements.stream()
            .map(ua -> {
                Map<String, Object> a = new HashMap<>();
                a.put("name", ua.getAchievement().getName());
                a.put("tier", ua.getAchievement().getTier());
                a.put("badgeIcon", ua.getAchievement().getBadgeIcon());
                a.put("unlockedAt", ua.getUnlockedAt());
                return a;
            })
            .collect(Collectors.toList()));
        
        return stats;
    }
    
    /**
     * Get leaderboard (top users by XP).
     */
    public List<Map<String, Object>> getLeaderboard(Integer limit) {
        List<User> topUsers = userRepository.findAll().stream()
            .sorted(Comparator.comparing(User::getExperiencePoints).reversed())
            .limit(limit != null ? limit : 10)
            .collect(Collectors.toList());
        
        return topUsers.stream()
            .map(user -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("userId", user.getId());
                entry.put("name", user.getName());
                entry.put("level", user.getLevel());
                entry.put("experiencePoints", user.getExperiencePoints());
                entry.put("itemsSaved", user.getItemsSaved());
                entry.put("totalCo2Saved", user.getTotalCo2SavedKg());
                return entry;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Update user's daily streak.
     */
    @Transactional
    public void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastActive = user.getLastActiveDate();
        
        if (lastActive == null) {
            user.setStreak(1);
            user.setLastActiveDate(today);
        } else if (lastActive.equals(today)) {
            // Already active today, no change
            return;
        } else if (lastActive.equals(today.minusDays(1))) {
            // Consecutive day
            user.setStreak(user.getStreak() + 1);
            user.setLastActiveDate(today);
            
            // Check streak achievements
            trackAchievementProgress(user, "STREAK", user.getStreak());
        } else {
            // Streak broken
            user.setStreak(1);
            user.setLastActiveDate(today);
        }
        
        userRepository.save(user);
    }
    
    // Helper methods
    
    private Achievement createAchievement(String name, String description, String type, 
                                         String tier, Integer pointsRequired, Integer xpReward, String badgeIcon) {
        Achievement achievement = new Achievement();
        achievement.setName(name);
        achievement.setDescription(description);
        achievement.setType(type);
        achievement.setTier(tier);
        achievement.setPointsRequired(pointsRequired);
        achievement.setXpReward(xpReward);
        achievement.setBadgeIcon(badgeIcon);
        achievement.setIsActive(true);
        return achievement;
    }
    
    private int calculateLevel(int xp) {
        int level = 1;
        while (xp >= calculateXPForLevel(level + 1)) {
            level++;
        }
        return level;
    }
    
    private int calculateXPForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        return (int) (BASE_XP_PER_LEVEL * Math.pow(LEVEL_MULTIPLIER, level - 1));
    }
}
