package com.groceriesapp.service;

import com.google.firebase.messaging.*;
import com.groceriesapp.model.Item;
import com.groceriesapp.model.User;
import com.groceriesapp.repository.ItemRepository;
import com.groceriesapp.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing push notifications via Firebase Cloud Messaging.
 */
@Service
public class NotificationService {
    
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    
    public NotificationService(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }
    
    /**
     * Register a user's FCM token for push notifications.
     */
    public boolean registerDeviceToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        user.setFcmToken(fcmToken);
        user.setNotificationsEnabled(true);
        userRepository.save(user);
        return true;
    }
    
    /**
     * Update notification preferences for a user.
     */
    public boolean updateNotificationPreferences(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        user.setNotificationsEnabled(enabled);
        userRepository.save(user);
        return true;
    }
    
    /**
     * Send a push notification to a specific user.
     */
    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        try {
            // Build the notification
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();
            
            // Build the message
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification);
            
            // Add custom data if provided
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            // Send the message
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            System.out.println("Successfully sent notification: " + response);
            
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send notification to a user by user ID.
     */
    public void sendNotificationToUser(Long userId, String title, String body, Map<String, String> data) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.getNotificationsEnabled() || user.getFcmToken() == null) {
            return;
        }
        
        sendNotification(user.getFcmToken(), title, body, data);
    }
    
    /**
     * Scheduled job to check for expiring items and send notifications.
     * Runs every day at 9:00 AM.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void checkExpiringItemsAndNotify() {
        System.out.println("Running scheduled task: Check expiring items and send notifications");
        
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate threeDaysFromNow = today.plusDays(3);
        LocalDate sevenDaysFromNow = today.plusDays(7);
        
        // Find all items expiring soon
        List<Item> allItems = itemRepository.findAll();
        
        // Group items by user
        Map<Long, Integer> userExpiringCounts = new HashMap<>();
        Map<Long, String> userExpiringItems = new HashMap<>();
        
        for (Item item : allItems) {
            LocalDate expirationDate = item.getExpirationDate();
            long daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate);
            
            // Check if item is expiring soon (within 7 days)
            if (daysUntilExpiration >= 0 && daysUntilExpiration <= 7) {
                Long userId = item.getAddedBy().getId();
                
                // Count expiring items per user
                userExpiringCounts.put(userId, userExpiringCounts.getOrDefault(userId, 0) + 1);
                
                // Collect item names
                String currentItems = userExpiringItems.getOrDefault(userId, "");
                if (!currentItems.isEmpty()) {
                    currentItems += ", ";
                }
                currentItems += item.getName() + " (" + daysUntilExpiration + " days)";
                userExpiringItems.put(userId, currentItems);
            }
        }
        
        // Send notifications to users
        for (Map.Entry<Long, Integer> entry : userExpiringCounts.entrySet()) {
            Long userId = entry.getKey();
            Integer count = entry.getValue();
            String items = userExpiringItems.get(userId);
            
            String title = count == 1 ? 
                    "1 Item Expiring Soon!" : 
                    count + " Items Expiring Soon!";
            
            String body = items.length() > 100 ? 
                    items.substring(0, 97) + "..." : 
                    items;
            
            Map<String, String> data = new HashMap<>();
            data.put("type", "expiring_items");
            data.put("count", String.valueOf(count));
            
            sendNotificationToUser(userId, title, body, data);
        }
    }
    
    /**
     * Send a notification when an item is about to expire (1 day before).
     */
    public void notifyItemExpiringSoon(Item item) {
        Long userId = item.getAddedBy().getId();
        
        String title = "⚠️ Item Expiring Tomorrow!";
        String body = item.getName() + " will expire tomorrow. Use it soon!";
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "item_expiring");
        data.put("itemId", String.valueOf(item.getId()));
        data.put("itemName", item.getName());
        
        sendNotificationToUser(userId, title, body, data);
    }
    
    /**
     * Send a notification when a user achieves a new level or achievement.
     */
    public void notifyAchievement(Long userId, String achievementName, String description) {
        String title = "🏆 Achievement Unlocked!";
        String body = achievementName + ": " + description;
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "achievement");
        data.put("achievementName", achievementName);
        
        sendNotificationToUser(userId, title, body, data);
    }
    
    /**
     * Send a notification when a user levels up.
     */
    public void notifyLevelUp(Long userId, int newLevel) {
        String title = "🎉 Level Up!";
        String body = "Congratulations! You've reached Level " + newLevel + "!";
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "level_up");
        data.put("level", String.valueOf(newLevel));
        
        sendNotificationToUser(userId, title, body, data);
    }
}
