package com.groceriesapp.model.nutrition;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_allergens")
public class UserAllergen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen_type", nullable = false)
    private AllergenType allergenType;
    
    @Column(name = "custom_allergen_name")
    private String customAllergenName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity = Severity.MODERATE;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
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
    
    public AllergenType getAllergenType() {
        return allergenType;
    }
    
    public void setAllergenType(AllergenType allergenType) {
        this.allergenType = allergenType;
    }
    
    public String getCustomAllergenName() {
        return customAllergenName;
    }
    
    public void setCustomAllergenName(String customAllergenName) {
        this.customAllergenName = customAllergenName;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    

}
