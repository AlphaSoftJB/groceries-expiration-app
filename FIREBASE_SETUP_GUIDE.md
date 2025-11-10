# Firebase Setup Guide for Groceries Expiration Tracking App

## Overview

This guide walks you through setting up Firebase for the Groceries Expiration Tracking App, including Cloud Messaging (push notifications), Analytics, and Crashlytics.

---

## Prerequisites

- Firebase account (create at [firebase.google.com](https://firebase.google.com))
- Google account
- Access to Apple Developer account (for iOS)
- Access to Google Play Console (for Android)

---

## Part 1: Create Firebase Project

### Step 1: Create New Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Enter project name: `groceries-expiration-tracker`
4. Enable Google Analytics (recommended)
5. Select or create Analytics account
6. Click **"Create project"**

### Step 2: Enable Required Services

1. **Cloud Messaging (FCM)**
   - Already enabled by default
   - No additional setup needed at this stage

2. **Analytics**
   - Already enabled if you selected it during project creation
   - Go to Analytics → Dashboard to verify

3. **Crashlytics** (Optional but recommended)
   - Go to Crashlytics in left menu
   - Click "Set up Crashlytics"
   - Follow the setup wizard

---

## Part 2: Android Configuration

### Step 1: Register Android App

1. In Firebase Console, click **"Add app"** → **Android**
2. Enter Android package name: `com.groceriesapp`
3. Enter app nickname: `Groceries Tracker Android`
4. Enter SHA-1 certificate fingerprint (for debugging):
   ```bash
   # Get debug keystore SHA-1
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
5. Click **"Register app"**

### Step 2: Download google-services.json

1. Download `google-services.json` file
2. Place it in your project:
   ```
   /home/ubuntu/GroceriesExpirationApp/frontend/android/app/google-services.json
   ```

### Step 3: Add Firebase SDK to Android

**File: `android/build.gradle`**
```gradle
buildscript {
    dependencies {
        // Add this line
        classpath 'com.google.gms:google-services:4.4.0'
    }
}
```

**File: `android/app/build.gradle`**
```gradle
// Add at the top
apply plugin: 'com.android.application'
apply plugin: 'com.google.gms.google-services'  // Add this line

dependencies {
    // Firebase BOM
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    
    // Firebase Cloud Messaging
    implementation 'com.google.firebase:firebase-messaging'
    
    // Firebase Analytics (optional)
    implementation 'com.google.firebase:firebase-analytics'
    
    // Firebase Crashlytics (optional)
    implementation 'com.google.firebase:firebase-crashlytics'
}
```

### Step 4: Configure AndroidManifest.xml

**File: `android/app/src/main/AndroidManifest.xml`**
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <application>
        <!-- Firebase Cloud Messaging Service -->
        <service
            android:name=".FirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
        
        <!-- Default notification channel -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="default_channel" />
            
        <!-- Default notification icon -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_notification" />
            
        <!-- Default notification color -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_color"
            android:resource="@color/notification_color" />
    </application>
</manifest>
```

### Step 5: Create Firebase Messaging Service

**File: `android/app/src/main/java/com/groceriesapp/FirebaseMessagingService.java`**
```java
package com.groceriesapp;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.util.Log;

public class FirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // Handle notification
        }

        // Check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // Handle data
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Send token to your server
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Implement this method to send token to your backend
    }
}
```

---

## Part 3: iOS Configuration

### Step 1: Register iOS App

1. In Firebase Console, click **"Add app"** → **iOS**
2. Enter iOS bundle ID: `com.groceriesapp`
3. Enter app nickname: `Groceries Tracker iOS`
4. Enter App Store ID (optional, can add later)
5. Click **"Register app"**

### Step 2: Download GoogleService-Info.plist

1. Download `GoogleService-Info.plist` file
2. Place it in your project:
   ```
   /home/ubuntu/GroceriesExpirationApp/frontend/ios/GroceriesApp/GoogleService-Info.plist
   ```
3. Add to Xcode project (drag and drop into Xcode)

### Step 3: Add Firebase SDK to iOS

**File: `ios/Podfile`**
```ruby
platform :ios, '13.0'

target 'GroceriesApp' do
  # Firebase
  pod 'Firebase/Core'
  pod 'Firebase/Messaging'
  pod 'Firebase/Analytics'
  pod 'Firebase/Crashlytics'  # Optional
  
  # Other dependencies...
end

post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '13.0'
    end
  end
end
```

Install pods:
```bash
cd ios
pod install
```

### Step 4: Configure AppDelegate

**File: `ios/GroceriesApp/AppDelegate.m` (Objective-C)**
```objc
#import "AppDelegate.h"
#import <Firebase.h>
#import <UserNotifications/UserNotifications.h>

@interface AppDelegate () <UNUserNotificationCenterDelegate>
@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  // Initialize Firebase
  [FIRApp configure];
  
  // Set notification delegate
  [UNUserNotificationCenter currentNotificationCenter].delegate = self;
  
  // Request notification permissions
  UNAuthorizationOptions authOptions = UNAuthorizationOptionAlert |
      UNAuthorizationOptionSound | UNAuthorizationOptionBadge;
  [[UNUserNotificationCenter currentNotificationCenter]
      requestAuthorizationWithOptions:authOptions
      completionHandler:^(BOOL granted, NSError * _Nullable error) {
        // Handle permission result
      }];
  
  [application registerForRemoteNotifications];
  
  return YES;
}

// Handle registration for remote notifications
- (void)application:(UIApplication *)application 
    didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
  [FIRMessaging messaging].APNSToken = deviceToken;
}

// Handle notification when app is in foreground
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
  completionHandler(UNNotificationPresentationOptionAlert | 
                   UNNotificationPresentationOptionBadge | 
                   UNNotificationPresentationOptionSound);
}

// Handle notification tap
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
didReceiveNotificationResponse:(UNNotificationResponse *)response
         withCompletionHandler:(void (^)(void))completionHandler {
  // Handle notification tap
  completionHandler();
}

@end
```

**Or File: `ios/GroceriesApp/AppDelegate.swift` (Swift)**
```swift
import UIKit
import Firebase
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(_ application: UIApplication, 
                    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        // Initialize Firebase
        FirebaseApp.configure()
        
        // Set notification delegate
        UNUserNotificationCenter.current().delegate = self
        
        // Request notification permissions
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { granted, error in
                // Handle permission result
            }
        )
        
        application.registerForRemoteNotifications()
        
        return true
    }
    
    // Handle registration for remote notifications
    func application(_ application: UIApplication, 
                    didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
    
    // Handle notification when app is in foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                              willPresent notification: UNNotification,
                              withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.alert, .badge, .sound])
    }
    
    // Handle notification tap
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                              didReceive response: UNNotificationResponse,
                              withCompletionHandler completionHandler: @escaping () -> Void) {
        // Handle notification tap
        completionHandler()
    }
}
```

### Step 5: Enable Push Notifications in Xcode

1. Open `ios/GroceriesApp.xcworkspace` in Xcode
2. Select project in navigator
3. Select target → **Signing & Capabilities**
4. Click **"+ Capability"**
5. Add **"Push Notifications"**
6. Add **"Background Modes"** and enable:
   - Remote notifications
   - Background fetch

### Step 6: Configure APNs Authentication

**Option 1: APNs Authentication Key (Recommended)**

1. Go to [Apple Developer Portal](https://developer.apple.com/account/)
2. Navigate to **Certificates, Identifiers & Profiles**
3. Click **Keys** → **+** (Create new key)
4. Enter key name: `Groceries App APNs Key`
5. Enable **Apple Push Notifications service (APNs)**
6. Click **Continue** → **Register**
7. Download the `.p8` file (save it securely!)
8. Note the **Key ID** and **Team ID**

Upload to Firebase:
1. Go to Firebase Console → Project Settings
2. Select **Cloud Messaging** tab
3. Under **iOS app configuration**, click **Upload**
4. Upload the `.p8` file
5. Enter Key ID and Team ID
6. Click **Upload**

**Option 2: APNs Certificate (Legacy)**

1. Create CSR in Keychain Access
2. Generate certificate in Apple Developer Portal
3. Download and install certificate
4. Export as `.p12` file
5. Upload to Firebase Console

---

## Part 4: React Native Integration

### Step 1: Install React Native Firebase

```bash
cd /home/ubuntu/GroceriesExpirationApp/frontend
npm install --save @react-native-firebase/app
npm install --save @react-native-firebase/messaging
npm install --save @react-native-firebase/analytics  # Optional
npm install --save @react-native-firebase/crashlytics  # Optional
```

### Step 2: Configure React Native Firebase

**File: `frontend/src/services/NotificationService.ts`**
```typescript
import messaging from '@react-native-firebase/messaging';
import { Platform } from 'react-native';

class NotificationService {
  async requestPermission(): Promise<boolean> {
    if (Platform.OS === 'ios') {
      const authStatus = await messaging().requestPermission();
      const enabled =
        authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
        authStatus === messaging.AuthorizationStatus.PROVISIONAL;
      return enabled;
    }
    return true; // Android doesn't need explicit permission request
  }

  async getToken(): Promise<string | null> {
    try {
      const token = await messaging().getToken();
      console.log('FCM Token:', token);
      return token;
    } catch (error) {
      console.error('Error getting FCM token:', error);
      return null;
    }
  }

  async setupNotificationListeners() {
    // Handle foreground notifications
    messaging().onMessage(async remoteMessage => {
      console.log('Foreground notification:', remoteMessage);
      // Display notification or update UI
    });

    // Handle background/quit state notifications
    messaging().setBackgroundMessageHandler(async remoteMessage => {
      console.log('Background notification:', remoteMessage);
      // Process notification
    });

    // Handle notification tap when app is in background
    messaging().onNotificationOpenedApp(remoteMessage => {
      console.log('Notification opened app from background:', remoteMessage);
      // Navigate to specific screen
    });

    // Handle notification tap when app was quit
    messaging()
      .getInitialNotification()
      .then(remoteMessage => {
        if (remoteMessage) {
          console.log('Notification opened app from quit state:', remoteMessage);
          // Navigate to specific screen
        }
      });

    // Handle token refresh
    messaging().onTokenRefresh(token => {
      console.log('FCM token refreshed:', token);
      // Send new token to backend
    });
  }

  async subscribeToTopic(topic: string) {
    await messaging().subscribeToTopic(topic);
    console.log(`Subscribed to topic: ${topic}`);
  }

  async unsubscribeFromTopic(topic: string) {
    await messaging().unsubscribeFromTopic(topic);
    console.log(`Unsubscribed from topic: ${topic}`);
  }
}

export default new NotificationService();
```

### Step 3: Initialize in App

**File: `frontend/App.tsx`**
```typescript
import React, { useEffect } from 'react';
import NotificationService from './src/services/NotificationService';

function App() {
  useEffect(() => {
    initializeNotifications();
  }, []);

  const initializeNotifications = async () => {
    // Request permission
    const hasPermission = await NotificationService.requestPermission();
    
    if (hasPermission) {
      // Get FCM token
      const token = await NotificationService.getToken();
      
      if (token) {
        // Send token to backend
        // await sendTokenToBackend(token);
      }
      
      // Setup listeners
      await NotificationService.setupNotificationListeners();
      
      // Subscribe to topics
      await NotificationService.subscribeToTopic('all_users');
    }
  };

  return (
    // Your app components
  );
}

export default App;
```

---

## Part 5: Backend Configuration

### Step 1: Download Firebase Admin SDK Key

1. Go to Firebase Console → Project Settings
2. Select **Service accounts** tab
3. Click **Generate new private key**
4. Download the JSON file
5. Save as `firebase-admin-sdk.json`
6. **IMPORTANT:** Keep this file secure! Never commit to version control!

### Step 2: Configure Backend

**File: `backend-java/src/main/resources/application.properties`**
```properties
# Firebase Configuration
firebase.credentials.path=classpath:firebase-admin-sdk.json
firebase.database.url=https://groceries-expiration-tracker.firebaseio.com
```

**File: `backend-java/src/main/java/com/groceries/config/FirebaseConfig.java`**
```java
package com.groceries.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.path}")
    private Resource firebaseCredentials;

    @Value("${firebase.database.url}")
    private String databaseUrl;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseCredentials.getInputStream()))
                    .setDatabaseUrl(databaseUrl)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully");
            }
        } catch (IOException e) {
            System.err.println("Error initializing Firebase Admin SDK: " + e.getMessage());
        }
    }
}
```

---

## Part 6: Testing

### Test Android Notifications

1. Build and run Android app:
   ```bash
   cd frontend
   npx react-native run-android
   ```

2. Send test notification from Firebase Console:
   - Go to Cloud Messaging
   - Click "Send your first message"
   - Enter notification title and text
   - Select app
   - Click "Send test message"
   - Enter FCM token from app logs
   - Click "Test"

### Test iOS Notifications

1. Build and run iOS app:
   ```bash
   cd frontend
   npx react-native run-ios
   ```

2. Test on physical device (notifications don't work in simulator)
3. Send test notification from Firebase Console (same as Android)

### Test Backend Notifications

```bash
# Test sending notification from backend
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { sendNotification(userId: \"123\", title: \"Test\", body: \"Test notification\") }"
  }'
```

---

## Part 7: Production Checklist

### Android
- [ ] Replace debug keystore with production keystore
- [ ] Add production SHA-1 to Firebase Console
- [ ] Test on multiple Android devices
- [ ] Verify notification icons and colors
- [ ] Test notification channels

### iOS
- [ ] Upload production APNs certificate/key
- [ ] Test on physical iOS devices
- [ ] Verify notification permissions
- [ ] Test background notification handling
- [ ] Test notification actions

### Backend
- [ ] Secure Firebase Admin SDK key
- [ ] Set up environment variables for production
- [ ] Configure notification scheduling
- [ ] Set up monitoring and logging
- [ ] Test notification delivery rates

---

## Troubleshooting

### Android Issues

**Problem:** Notifications not received
- Check `google-services.json` is in correct location
- Verify package name matches Firebase Console
- Check notification permissions in device settings
- Review logcat for errors

**Problem:** Build fails
- Run `./gradlew clean`
- Check Google Services plugin version
- Verify all dependencies are compatible

### iOS Issues

**Problem:** Notifications not received
- Verify `GoogleService-Info.plist` is added to Xcode
- Check bundle ID matches Firebase Console
- Ensure APNs certificate/key is uploaded
- Test on physical device (not simulator)

**Problem:** Build fails
- Run `pod install` again
- Clean build folder in Xcode
- Check deployment target version

### Common Issues

**Problem:** Token not generated
- Check internet connection
- Verify Firebase is initialized
- Check app permissions

**Problem:** Backend can't send notifications
- Verify Firebase Admin SDK key is valid
- Check service account permissions
- Review backend logs for errors

---

## Security Best Practices

1. **Never commit Firebase config files to version control**
   - Add to `.gitignore`:
     ```
     google-services.json
     GoogleService-Info.plist
     firebase-admin-sdk.json
     ```

2. **Use environment variables for sensitive data**
   ```bash
   export FIREBASE_CREDENTIALS_PATH=/path/to/firebase-admin-sdk.json
   ```

3. **Restrict API keys in Firebase Console**
   - Set application restrictions
   - Set API restrictions

4. **Enable App Check** (recommended for production)
   - Protects backend from abuse
   - Verifies requests come from your app

---

## Additional Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [React Native Firebase](https://rnfirebase.io/)
- [FCM Documentation](https://firebase.google.com/docs/cloud-messaging)
- [APNs Documentation](https://developer.apple.com/documentation/usernotifications)

---

## Support

For issues or questions:
- Firebase Support: https://firebase.google.com/support
- React Native Firebase: https://github.com/invertase/react-native-firebase
- Project Issues: [Your GitHub Issues URL]

---

**Last Updated:** November 6, 2025  
**Version:** 1.0.0
