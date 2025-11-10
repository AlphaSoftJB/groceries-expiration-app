package com.groceriesapp.model.nutrition;

/**
 * Enum representing the user's action in response to an allergen alert.
 */
public enum UserAction {
    PROCEEDED,      // User chose to proceed despite the allergen
    CANCELLED,      // User cancelled the action
    IGNORED         // User dismissed without action
}
