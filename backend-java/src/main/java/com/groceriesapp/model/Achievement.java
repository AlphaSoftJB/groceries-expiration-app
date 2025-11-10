package com.groceriesapp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievements")
@Data
public class Achievement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String type; // WASTE_WARRIOR, ECO_CHAMPION, SCAN_MASTER, etc.
    
    @Column(nullable = false)
    private String tier; // BRONZE, SILVER, GOLD, PLATINUM
    
    @Column(nullable = false)
    private Integer pointsRequired;
    
    @Column(nullable = false)
    private Integer xpReward;
    
    private String badgeIcon; // URL or icon name
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
