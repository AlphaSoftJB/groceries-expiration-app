import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Switch,
  TouchableOpacity,
  ScrollView,
  Alert,
} from 'react-native';
import { useMutation, gql } from '@apollo/client';
import {
  initializeNotifications,
  updateNotificationPreferences,
  getFCMToken,
} from '../services/notificationService';
import apolloClient from '../services/apolloClient';

const UPDATE_NOTIFICATION_PREFERENCES = gql`
  mutation UpdateNotificationPreferences($userId: ID!, $enabled: Boolean!) {
    updateNotificationPreferences(userId: $userId, enabled: $enabled)
  }
`;

/**
 * Notification Settings Screen
 * 
 * Allows users to:
 * - Enable/disable push notifications
 * - View notification status
 * - Test notifications
 * - Manage notification preferences
 */
export default function NotificationSettingsScreen() {
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [fcmToken, setFcmToken] = useState<string | null>(null);
  const [isInitializing, setIsInitializing] = useState(false);
  
  // Hardcoded user ID for now
  const USER_ID = "1";
  
  const [updatePreferences] = useMutation(UPDATE_NOTIFICATION_PREFERENCES);
  
  useEffect(() => {
    loadNotificationStatus();
  }, []);
  
  const loadNotificationStatus = async () => {
    try {
      const token = await getFCMToken();
      setFcmToken(token);
    } catch (error) {
      console.error('Error loading notification status:', error);
    }
  };
  
  const handleToggleNotifications = async (value: boolean) => {
    try {
      if (value) {
        // Enable notifications
        setIsInitializing(true);
        const success = await initializeNotifications(USER_ID, apolloClient);
        setIsInitializing(false);
        
        if (success) {
          setNotificationsEnabled(true);
          Alert.alert('Success', 'Notifications enabled successfully!');
          loadNotificationStatus();
        } else {
          Alert.alert('Error', 'Failed to enable notifications');
        }
      } else {
        // Disable notifications
        const result = await updateNotificationPreferences(
          USER_ID,
          false,
          apolloClient
        );
        
        if (result) {
          setNotificationsEnabled(false);
          Alert.alert('Success', 'Notifications disabled');
        } else {
          Alert.alert('Error', 'Failed to disable notifications');
        }
      }
    } catch (error) {
      setIsInitializing(false);
      Alert.alert('Error', 'An error occurred while updating notification settings');
      console.error('Error toggling notifications:', error);
    }
  };
  
  const handleTestNotification = () => {
    Alert.alert(
      'Test Notification',
      'This is how a notification would appear on your device!',
      [{ text: 'OK' }]
    );
  };
  
  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Notification Settings</Text>
        <Text style={styles.subtitle}>
          Manage how you receive alerts about expiring items
        </Text>
      </View>
      
      {/* Main Toggle */}
      <View style={styles.section}>
        <View style={styles.settingRow}>
          <View style={styles.settingInfo}>
            <Text style={styles.settingTitle}>Push Notifications</Text>
            <Text style={styles.settingDescription}>
              Receive alerts about expiring items
            </Text>
          </View>
          <Switch
            value={notificationsEnabled}
            onValueChange={handleToggleNotifications}
            disabled={isInitializing}
          />
        </View>
      </View>
      
      {/* Notification Types */}
      {notificationsEnabled && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Notification Types</Text>
          
          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingTitle}>Expiring Items</Text>
              <Text style={styles.settingDescription}>
                Daily summary of items expiring soon
              </Text>
            </View>
            <Switch value={true} disabled />
          </View>
          
          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingTitle}>Achievements</Text>
              <Text style={styles.settingDescription}>
                When you unlock new badges or level up
              </Text>
            </View>
            <Switch value={true} disabled />
          </View>
          
          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingTitle}>Shopping Reminders</Text>
              <Text style={styles.settingDescription}>
                Suggestions for items to buy
              </Text>
            </View>
            <Switch value={true} disabled />
          </View>
        </View>
      )}
      
      {/* Notification Schedule */}
      {notificationsEnabled && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Schedule</Text>
          
          <TouchableOpacity style={styles.scheduleOption}>
            <Text style={styles.scheduleLabel}>Daily Summary Time</Text>
            <Text style={styles.scheduleValue}>9:00 AM</Text>
          </TouchableOpacity>
          
          <Text style={styles.scheduleNote}>
            You'll receive a daily summary of expiring items at this time
          </Text>
        </View>
      )}
      
      {/* Status */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Status</Text>
        
        <View style={styles.statusRow}>
          <Text style={styles.statusLabel}>Notification Status:</Text>
          <Text style={[
            styles.statusValue,
            notificationsEnabled ? styles.statusEnabled : styles.statusDisabled
          ]}>
            {notificationsEnabled ? 'Enabled' : 'Disabled'}
          </Text>
        </View>
        
        {fcmToken && (
          <View style={styles.statusRow}>
            <Text style={styles.statusLabel}>Device Token:</Text>
            <Text style={styles.statusValue} numberOfLines={1}>
              {fcmToken.substring(0, 20)}...
            </Text>
          </View>
        )}
      </View>
      
      {/* Test Button */}
      {notificationsEnabled && (
        <TouchableOpacity
          style={styles.testButton}
          onPress={handleTestNotification}
        >
          <Text style={styles.testButtonText}>Test Notification</Text>
        </TouchableOpacity>
      )}
      
      {/* Info */}
      <View style={styles.infoSection}>
        <Text style={styles.infoTitle}>About Notifications</Text>
        <Text style={styles.infoText}>
          • Daily summaries are sent at 9:00 AM{'\n'}
          • Urgent alerts for items expiring tomorrow{'\n'}
          • Achievement notifications when you level up{'\n'}
          • Shopping suggestions based on your usage
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#FF9800',
    padding: 20,
    paddingTop: 40,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 5,
  },
  subtitle: {
    fontSize: 14,
    color: '#fff',
    opacity: 0.9,
  },
  section: {
    backgroundColor: '#fff',
    margin: 15,
    padding: 15,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333',
  },
  settingRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  settingInfo: {
    flex: 1,
    marginRight: 15,
  },
  settingTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 3,
  },
  settingDescription: {
    fontSize: 13,
    color: '#666',
  },
  scheduleOption: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  scheduleLabel: {
    fontSize: 16,
    color: '#333',
  },
  scheduleValue: {
    fontSize: 16,
    color: '#FF9800',
    fontWeight: '600',
  },
  scheduleNote: {
    fontSize: 13,
    color: '#666',
    marginTop: 10,
    fontStyle: 'italic',
  },
  statusRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
  },
  statusLabel: {
    fontSize: 14,
    color: '#666',
  },
  statusValue: {
    fontSize: 14,
    fontWeight: '600',
  },
  statusEnabled: {
    color: '#4CAF50',
  },
  statusDisabled: {
    color: '#F44336',
  },
  testButton: {
    backgroundColor: '#2196F3',
    margin: 15,
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
  },
  testButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  infoSection: {
    margin: 15,
    padding: 15,
    backgroundColor: '#E3F2FD',
    borderRadius: 10,
    borderLeftWidth: 4,
    borderLeftColor: '#2196F3',
  },
  infoTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#1976D2',
    marginBottom: 10,
  },
  infoText: {
    fontSize: 14,
    color: '#333',
    lineHeight: 22,
  },
});
