package com.groceriesapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id")
    private Household household;
    
    @Column(name = "total_co2_saved_kg")
    private Double totalCo2SavedKg = 0.0;
    
    // Gamification fields
    @Column(nullable = false)
    private Integer level = 1;
    
    @Column(name = "experience_points", nullable = false)
    private Integer experiencePoints = 0;
    
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;
    
    @Column(name = "items_saved", nullable = false)
    private Integer itemsSaved = 0;
    
    @Column(name = "items_scanned", nullable = false)
    private Integer itemsScanned = 0;
    
    @Column(nullable = false)
    private Integer streak = 0; // Days of consecutive app usage
    
    @Column(name = "last_active_date")
    private java.time.LocalDate lastActiveDate;
    
    // Push notification fields
    @Column(name = "fcm_token")
    private String fcmToken;
    
    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
