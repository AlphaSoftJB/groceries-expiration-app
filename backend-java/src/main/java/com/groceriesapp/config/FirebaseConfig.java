package com.groceriesapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Configuration class for Firebase Admin SDK.
 * 
 * Setup Instructions:
 * 1. Go to Firebase Console (https://console.firebase.google.com/)
 * 2. Create a new project or select existing project
 * 3. Go to Project Settings > Service Accounts
 * 4. Click "Generate New Private Key"
 * 5. Save the JSON file as "firebase-service-account.json" in the resources folder
 * 6. Or set the GOOGLE_APPLICATION_CREDENTIALS environment variable
 */
@Configuration
@EnableScheduling
public class FirebaseConfig {
    
    @PostConstruct
    public void initialize() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                // Try to load from environment variable first
                String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                
                FirebaseOptions options;
                
                if (credentialsPath != null && !credentialsPath.isEmpty()) {
                    // Load from environment variable path
                    FileInputStream serviceAccount = new FileInputStream(credentialsPath);
                    options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                } else {
                    // Try to load from resources folder
                    try {
                        FileInputStream serviceAccount = new FileInputStream(
                                "src/main/resources/firebase-service-account.json"
                        );
                        options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                .build();
                    } catch (IOException e) {
                        // If no credentials found, use default (for development only)
                        System.err.println("WARNING: Firebase credentials not found. Notifications will not work.");
                        System.err.println("Please set GOOGLE_APPLICATION_CREDENTIALS or add firebase-service-account.json");
                        return;
                    }
                }
                
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully");
            }
        } catch (IOException e) {
            System.err.println("Error initializing Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
