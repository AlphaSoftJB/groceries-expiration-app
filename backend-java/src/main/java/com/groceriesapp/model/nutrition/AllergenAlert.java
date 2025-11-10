package com.groceriesapp.model.nutrition;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "allergen_alerts")
public class AllergenAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    
    private Long userId;
    
    private Long itemId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen_type", nullable = false)
    private AllergenType allergenType;
    
    @Column(name = "allergen_name")
    private String allergenName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AlertStatus status = AlertStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_action")
    private UserAction userAction;
    
    @Column(name = "user_action_at")
    private LocalDateTime userActionAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public AllergenType getAllergenType() {
        return allergenType;
    }
    
    public void setAllergenType(AllergenType allergenType) {
        this.allergenType = allergenType;
    }
    
    public String getAllergenName() {
        return allergenName;
    }
    
    public void setAllergenName(String allergenName) {
        this.allergenName = allergenName;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    public AlertStatus getStatus() {
        return status;
    }
    
    public void setStatus(AlertStatus status) {
        this.status = status;
    }
    
    public UserAction getUserAction() {
        return userAction;
    }
    
    public void setUserAction(UserAction userAction) {
        this.userAction = userAction;
    }
    
    public LocalDateTime getUserActionAt() {
        return userActionAt;
    }
    
    public void setUserActionAt(LocalDateTime userActionAt) {
        this.userActionAt = userActionAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Enums
    
    
}
