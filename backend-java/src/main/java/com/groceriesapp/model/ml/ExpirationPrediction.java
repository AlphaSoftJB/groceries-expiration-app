package com.groceriesapp.model.ml;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expiration_predictions")
public class ExpirationPrediction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "storage_location")
    private String storageLocation;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "labeled_expiration_date")
    private LocalDate labeledExpirationDate;
    
    @Column(name = "predicted_expiration_date")
    private LocalDate predictedExpirationDate;
    
    @Column(name = "actual_expiration_date")
    private LocalDate actualExpirationDate;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Column(name = "temperature_avg")
    private Double temperatureAvg;
    
    @Column(name = "humidity_avg")
    private Double humidityAvg;
    
    @Column(name = "open_count")
    private Integer openCount;
    
    @Column(name = "prediction_accuracy")
    private Double predictionAccuracy;
    
    @Column(name = "model_version")
    private String modelVersion;
    
    @Column(name = "features", columnDefinition = "TEXT")
    private String features; // JSON string of feature values
    
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
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getStorageLocation() {
        return storageLocation;
    }
    
    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public LocalDate getLabeledExpirationDate() {
        return labeledExpirationDate;
    }
    
    public void setLabeledExpirationDate(LocalDate labeledExpirationDate) {
        this.labeledExpirationDate = labeledExpirationDate;
    }
    
    public LocalDate getPredictedExpirationDate() {
        return predictedExpirationDate;
    }
    
    public void setPredictedExpirationDate(LocalDate predictedExpirationDate) {
        this.predictedExpirationDate = predictedExpirationDate;
    }
    
    public LocalDate getActualExpirationDate() {
        return actualExpirationDate;
    }
    
    public void setActualExpirationDate(LocalDate actualExpirationDate) {
        this.actualExpirationDate = actualExpirationDate;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public Double getTemperatureAvg() {
        return temperatureAvg;
    }
    
    public void setTemperatureAvg(Double temperatureAvg) {
        this.temperatureAvg = temperatureAvg;
    }
    
    public Double getHumidityAvg() {
        return humidityAvg;
    }
    
    public void setHumidityAvg(Double humidityAvg) {
        this.humidityAvg = humidityAvg;
    }
    
    public Integer getOpenCount() {
        return openCount;
    }
    
    public void setOpenCount(Integer openCount) {
        this.openCount = openCount;
    }
    
    public Double getPredictionAccuracy() {
        return predictionAccuracy;
    }
    
    public void setPredictionAccuracy(Double predictionAccuracy) {
        this.predictionAccuracy = predictionAccuracy;
    }
    
    public String getModelVersion() {
        return modelVersion;
    }
    
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    
    public String getFeatures() {
        return features;
    }
    
    public void setFeatures(String features) {
        this.features = features;
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
