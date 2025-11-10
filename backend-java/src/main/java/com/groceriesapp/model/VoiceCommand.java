package com.groceriesapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voice_commands")
public class VoiceCommand {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "command_text", columnDefinition = "TEXT", nullable = false)
    private String commandText;
    
    @Column(name = "command_type")
    @Enumerated(EnumType.STRING)
    private CommandType commandType;
    
    @Column(name = "intent")
    private String intent;
    
    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parameters; // JSON string
    
    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;
    
    @Column(name = "is_successful")
    private Boolean isSuccessful;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "processing_time_ms")
    private Integer processingTimeMs;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum CommandType {
        ADD_ITEM,
        CHECK_EXPIRATION,
        GET_RECIPE,
        CREATE_SHOPPING_LIST,
        LOG_MEAL,
        CHECK_ALLERGEN,
        GET_NUTRITION,
        PLAN_MEAL,
        OTHER
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
    
    public String getCommandText() {
        return commandText;
    }
    
    public void setCommandText(String commandText) {
        this.commandText = commandText;
    }
    
    public CommandType getCommandType() {
        return commandType;
    }
    
    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }
    
    public String getIntent() {
        return intent;
    }
    
    public void setIntent(String intent) {
        this.intent = intent;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    public String getResponseText() {
        return responseText;
    }
    
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
    
    public Boolean getIsSuccessful() {
        return isSuccessful;
    }
    
    public void setIsSuccessful(Boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Integer getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(Integer processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
