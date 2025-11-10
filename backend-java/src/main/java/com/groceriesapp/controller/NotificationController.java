package com.groceriesapp.controller;

import com.groceriesapp.service.NotificationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Controller for Push Notifications.
 */
@Controller
public class NotificationController {
    
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Register a device token for push notifications.
     */
    @MutationMapping
    public Boolean registerDeviceToken(@Argument Long userId, @Argument String fcmToken) {
        return notificationService.registerDeviceToken(userId, fcmToken);
    }
    
    /**
     * Update notification preferences.
     */
    @MutationMapping
    public Boolean updateNotificationPreferences(@Argument Long userId, @Argument Boolean enabled) {
        return notificationService.updateNotificationPreferences(userId, enabled);
    }
}
