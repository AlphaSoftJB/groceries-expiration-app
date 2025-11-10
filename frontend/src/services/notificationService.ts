/**
 * Notification Service for React Native
 * 
 * This service handles push notifications using Firebase Cloud Messaging (FCM).
 * 
 * Setup Instructions:
 * 1. Install dependencies:
 *    npm install @react-native-firebase/app @react-native-firebase/messaging
 * 
 * 2. For iOS:
 *    - Add GoogleService-Info.plist to ios/ folder
 *    - Run: cd ios && pod install
 *    - Enable Push Notifications in Xcode capabilities
 * 
 * 3. For Android:
 *    - Add google-services.json to android/app/
 *    - Update android/build.gradle and android/app/build.gradle
 * 
 * 4. Request notification permissions
 * 5. Get FCM token and register with backend
 */

import { Platform, Alert } from 'react-native';
import { gql } from '@apollo/client';

// GraphQL mutation for registering device token
export const REGISTER_DEVICE_TOKEN = gql`
  mutation RegisterDeviceToken($userId: ID!, $fcmToken: String!) {
    registerDeviceToken(userId: $userId, fcmToken: $fcmToken)
  }
`;

export const UPDATE_NOTIFICATION_PREFERENCES = gql`
  mutation UpdateNotificationPreferences($userId: ID!, $enabled: Boolean!) {
    updateNotificationPreferences(userId: $userId, enabled: $enabled)
  }
`;

/**
 * Request notification permissions from the user.
 * Required for iOS, optional for Android.
 */
export async function requestNotificationPermission(): Promise<boolean> {
  try {
    // In a real app with @react-native-firebase/messaging:
    // const authStatus = await messaging().requestPermission();
    // const enabled =
    //   authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
    //   authStatus === messaging.AuthorizationStatus.PROVISIONAL;
    
    // For now, simulate permission request
    return new Promise((resolve) => {
      Alert.alert(
        'Enable Notifications',
        'Allow push notifications to get alerts about expiring items?',
        [
          {
            text: 'Don\'t Allow',
            onPress: () => resolve(false),
            style: 'cancel',
          },
          {
            text: 'Allow',
            onPress: () => resolve(true),
          },
        ]
      );
    });
  } catch (error) {
    console.error('Error requesting notification permission:', error);
    return false;
  }
}

/**
 * Get the FCM token for this device.
 */
export async function getFCMToken(): Promise<string | null> {
  try {
    // In a real app with @react-native-firebase/messaging:
    // const token = await messaging().getToken();
    // return token;
    
    // For now, generate a mock token
    const mockToken = `mock_fcm_token_${Platform.OS}_${Date.now()}`;
    console.log('Mock FCM Token:', mockToken);
    return mockToken;
  } catch (error) {
    console.error('Error getting FCM token:', error);
    return null;
  }
}

/**
 * Register the device token with the backend.
 */
export async function registerDeviceWithBackend(
  userId: string,
  fcmToken: string,
  apolloClient: any
): Promise<boolean> {
  try {
    const { data } = await apolloClient.mutate({
      mutation: REGISTER_DEVICE_TOKEN,
      variables: { userId, fcmToken },
    });
    
    return data.registerDeviceToken;
  } catch (error) {
    console.error('Error registering device token:', error);
    return false;
  }
}

/**
 * Initialize notification service.
 * Call this when the app starts and user is logged in.
 */
export async function initializeNotifications(
  userId: string,
  apolloClient: any
): Promise<boolean> {
  try {
    // Request permission
    const hasPermission = await requestNotificationPermission();
    if (!hasPermission) {
      console.log('Notification permission denied');
      return false;
    }
    
    // Get FCM token
    const fcmToken = await getFCMToken();
    if (!fcmToken) {
      console.error('Failed to get FCM token');
      return false;
    }
    
    // Register with backend
    const registered = await registerDeviceWithBackend(userId, fcmToken, apolloClient);
    if (!registered) {
      console.error('Failed to register device with backend');
      return false;
    }
    
    console.log('Notifications initialized successfully');
    return true;
  } catch (error) {
    console.error('Error initializing notifications:', error);
    return false;
  }
}

/**
 * Handle foreground notifications.
 * This is called when a notification arrives while the app is open.
 */
export function setupForegroundNotificationHandler() {
  // In a real app with @react-native-firebase/messaging:
  // messaging().onMessage(async remoteMessage => {
  //   console.log('Foreground notification:', remoteMessage);
  //   
  //   Alert.alert(
  //     remoteMessage.notification?.title || 'Notification',
  //     remoteMessage.notification?.body || ''
  //   );
  // });
  
  console.log('Foreground notification handler set up');
}

/**
 * Handle background notifications.
 * This is called when a notification is tapped while the app is in background.
 */
export function setupBackgroundNotificationHandler() {
  // In a real app with @react-native-firebase/messaging:
  // messaging().onNotificationOpenedApp(remoteMessage => {
  //   console.log('Notification opened app:', remoteMessage);
  //   
  //   // Navigate to appropriate screen based on notification data
  //   if (remoteMessage.data?.type === 'expiring_items') {
  //     // Navigate to home screen
  //   } else if (remoteMessage.data?.type === 'achievement') {
  //     // Navigate to impact dashboard
  //   }
  // });
  
  console.log('Background notification handler set up');
}

/**
 * Check if the app was opened from a notification (when app was quit).
 */
export async function checkInitialNotification() {
  // In a real app with @react-native-firebase/messaging:
  // const remoteMessage = await messaging().getInitialNotification();
  // if (remoteMessage) {
  //   console.log('App opened from notification:', remoteMessage);
  //   // Handle navigation
  // }
  
  console.log('Checked initial notification');
}

/**
 * Update notification preferences.
 */
export async function updateNotificationPreferences(
  userId: string,
  enabled: boolean,
  apolloClient: any
): Promise<boolean> {
  try {
    const { data } = await apolloClient.mutate({
      mutation: UPDATE_NOTIFICATION_PREFERENCES,
      variables: { userId, enabled },
    });
    
    return data.updateNotificationPreferences;
  } catch (error) {
    console.error('Error updating notification preferences:', error);
    return false;
  }
}

/**
 * Complete setup guide for implementing real notifications:
 * 
 * 1. Install Firebase packages:
 *    npm install @react-native-firebase/app @react-native-firebase/messaging
 * 
 * 2. iOS Setup:
 *    - Create Firebase project at https://console.firebase.google.com/
 *    - Add iOS app to Firebase project
 *    - Download GoogleService-Info.plist
 *    - Add to ios/ folder in Xcode
 *    - Enable Push Notifications capability in Xcode
 *    - Upload APNs certificate to Firebase Console
 *    - Run: cd ios && pod install
 * 
 * 3. Android Setup:
 *    - Add Android app to Firebase project
 *    - Download google-services.json
 *    - Place in android/app/
 *    - Update android/build.gradle:
 *      dependencies {
 *        classpath 'com.google.gms:google-services:4.3.15'
 *      }
 *    - Update android/app/build.gradle:
 *      apply plugin: 'com.google.gms.google-services'
 * 
 * 4. Update App.tsx to initialize notifications on app start
 * 
 * 5. Test notifications using Firebase Console
 */
