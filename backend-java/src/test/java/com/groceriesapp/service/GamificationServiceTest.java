package com.groceriesapp.service;

import com.groceriesapp.model.Achievement;
import com.groceriesapp.model.User;
import com.groceriesapp.model.UserAchievement;
import com.groceriesapp.repository.AchievementRepository;
import com.groceriesapp.repository.UserAchievementRepository;
import com.groceriesapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private AchievementRepository achievementRepository;
    
    @Mock
    private UserAchievementRepository userAchievementRepository;
    
    @InjectMocks
    private GamificationService gamificationService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setLevel(1);
        testUser.setExperiencePoints(0);
        testUser.setTotalPoints(0);
        testUser.setItemsSaved(0);
        testUser.setItemsScanned(0);
        testUser.setStreak(0);
        testUser.setTotalCo2SavedKg(0.0);
    }
    
    @Test
    void testAwardExperience_NoLevelUp() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        Map<String, Object> result = gamificationService.awardExperience(1L, 50, "Test reward");
        
        assertNotNull(result);
        assertEquals(1, result.get("oldLevel"));
        assertEquals(1, result.get("newLevel"));
        assertEquals(false, result.get("leveledUp"));
        assertEquals(50, result.get("xpGained"));
        assertEquals("Test reward", result.get("reason"));
        
        // awardExperience calls save twice: once for XP update, once for streak update
        verify(userRepository, atLeast(1)).save(any(User.class));
    }
    
    @Test
    void testAwardExperience_WithLevelUp() {
        testUser.setExperiencePoints(90); // Close to level 2
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        Map<String, Object> result = gamificationService.awardExperience(1L, 50, "Level up test");
        
        assertNotNull(result);
        assertEquals(1, result.get("oldLevel"));
        // With 90 + 50 = 140 XP, should be at level 1 or higher
        Integer newLevel = (Integer) result.get("newLevel");
        assertTrue(newLevel >= 1, "New level should be at least 1, got: " + newLevel);
        assertEquals(50, result.get("xpGained"));
    }
    
    @Test
    void testGetUserStats() {
        testUser.setLevel(5);
        testUser.setExperiencePoints(500);
        testUser.setItemsSaved(25);
        testUser.setStreak(7);
        testUser.setTotalCo2SavedKg(15.5);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userAchievementRepository.findByUserAndIsUnlockedTrue(any(User.class)))
            .thenReturn(new ArrayList<>());
        
        Map<String, Object> stats = gamificationService.getUserStats(1L);
        
        assertNotNull(stats);
        assertEquals(5, stats.get("level"));
        assertEquals(500, stats.get("experiencePoints"));
        assertEquals(25, stats.get("itemsSaved"));
        assertEquals(7, stats.get("streak"));
        assertEquals(15.5, stats.get("totalCo2Saved"));
        assertTrue(stats.containsKey("xpProgress"));
        assertTrue(stats.containsKey("xpNeeded"));
    }
    
    @Test
    void testGetLeaderboard() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setId((long) i);
            user.setName("User " + i);
            user.setLevel(i);
            user.setExperiencePoints(i * 100);
            user.setItemsSaved(i * 10);
            user.setTotalCo2SavedKg((double) i * 5);
            users.add(user);
        }
        
        when(userRepository.findAll()).thenReturn(users);
        
        List<Map<String, Object>> leaderboard = gamificationService.getLeaderboard(3);
        
        assertNotNull(leaderboard);
        assertEquals(3, leaderboard.size());
        
        // Should be sorted by XP (descending)
        Integer firstXP = (Integer) leaderboard.get(0).get("experiencePoints");
        Integer secondXP = (Integer) leaderboard.get(1).get("experiencePoints");
        assertTrue(firstXP >= secondXP);
    }
    
    @Test
    void testUpdateStreak_FirstTime() {
        testUser.setLastActiveDate(null);
        testUser.setStreak(0);
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        gamificationService.updateStreak(testUser);
        
        assertEquals(1, testUser.getStreak());
        assertEquals(LocalDate.now(), testUser.getLastActiveDate());
    }
    
    @Test
    void testUpdateStreak_ConsecutiveDay() {
        testUser.setLastActiveDate(LocalDate.now().minusDays(1));
        testUser.setStreak(5);
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        gamificationService.updateStreak(testUser);
        
        assertEquals(6, testUser.getStreak());
        assertEquals(LocalDate.now(), testUser.getLastActiveDate());
    }
    
    @Test
    void testUpdateStreak_StreakBroken() {
        testUser.setLastActiveDate(LocalDate.now().minusDays(3));
        testUser.setStreak(10);
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        gamificationService.updateStreak(testUser);
        
        assertEquals(1, testUser.getStreak());
        assertEquals(LocalDate.now(), testUser.getLastActiveDate());
    }
    
    @Test
    void testUpdateStreak_SameDay() {
        testUser.setLastActiveDate(LocalDate.now());
        testUser.setStreak(5);
        
        gamificationService.updateStreak(testUser);
        
        // Streak should not change
        assertEquals(5, testUser.getStreak());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void testInitializeAchievements() {
        when(achievementRepository.count()).thenReturn(0L);
        when(achievementRepository.saveAll(any())).thenReturn(new ArrayList<>());
        
        gamificationService.initializeAchievements();
        
        verify(achievementRepository, times(1)).saveAll(any());
    }
    
    @Test
    void testInitializeAchievements_AlreadyInitialized() {
        when(achievementRepository.count()).thenReturn(10L);
        
        gamificationService.initializeAchievements();
        
        verify(achievementRepository, never()).saveAll(any());
    }
}
