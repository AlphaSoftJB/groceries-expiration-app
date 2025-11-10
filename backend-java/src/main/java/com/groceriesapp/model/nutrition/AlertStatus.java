package com.groceriesapp.model.nutrition;

/**
 * Enum representing the status of an allergen alert.
 */
public enum AlertStatus {
    PENDING,        // Alert created, user hasn't responded
    ACKNOWLEDGED,   // User has seen and responded to the alert
    DISMISSED       // User dismissed the alert
}
