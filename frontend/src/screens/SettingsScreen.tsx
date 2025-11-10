import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Switch,
  Alert,
} from 'react-native';
import { useTranslation } from 'react-i18next';
import { saveLanguagePreference, getAvailableLanguages } from '../i18n';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

type RootStackParamList = {
  Settings: undefined;
  AllergenManagement: undefined;
  DietaryPreferences: undefined;
  AllergenAlerts: undefined;
};

type SettingsScreenProps = NativeStackScreenProps<RootStackParamList, 'Settings'>;

const SettingsScreen: React.FC<SettingsScreenProps> = ({ navigation }) => {
  const { t, i18n } = useTranslation();
  const [currentLanguage, setCurrentLanguage] = useState(i18n.language);
  
  // Accessibility settings
  const [highContrast, setHighContrast] = useState(false);
  const [largeText, setLargeText] = useState(false);
  const [screenReader, setScreenReader] = useState(false);
  const [voiceCommands, setVoiceCommands] = useState(false);
  const [colorBlindMode, setColorBlindMode] = useState(false);
  
  // Notification settings
  const [expirationAlerts, setExpirationAlerts] = useState(true);
  const [achievementNotifications, setAchievementNotifications] = useState(true);
  const [dailySummary, setDailySummary] = useState(true);
  
  // Child mode settings
  const [childMode, setChildMode] = useState(false);
  const [parentalControls, setParentalControls] = useState(false);

  const handleLanguageChange = async (languageCode: string) => {
    try {
      await saveLanguagePreference(languageCode);
      setCurrentLanguage(languageCode);
      Alert.alert(
        t('success.settingsSaved'),
        t('settings.language') + ': ' + languageCode.toUpperCase()
      );
    } catch (error) {
      Alert.alert(t('common.error'), t('error.generic'));
    }
  };

  const renderLanguageOption = (language: { code: string; name: string; nativeName: string }) => (
    <TouchableOpacity
      key={language.code}
      style={[
        styles.languageOption,
        currentLanguage === language.code && styles.selectedLanguage,
      ]}
      onPress={() => handleLanguageChange(language.code)}
      accessibilityLabel={`${language.name} - ${language.nativeName}`}
      accessibilityRole="button"
    >
      <View style={styles.languageInfo}>
        <Text style={styles.languageName}>{language.name}</Text>
        <Text style={styles.languageNative}>{language.nativeName}</Text>
      </View>
      {currentLanguage === language.code && (
        <Text style={styles.checkmark}>✓</Text>
      )}
    </TouchableOpacity>
  );

  const renderSettingItem = (
    label: string,
    value: boolean,
    onValueChange: (value: boolean) => void,
    description?: string
  ) => (
    <View style={styles.settingItem} accessibilityRole="switch">
      <View style={styles.settingInfo}>
        <Text style={styles.settingLabel}>{label}</Text>
        {description && (
          <Text style={styles.settingDescription}>{description}</Text>
        )}
      </View>
      <Switch
        value={value}
        onValueChange={onValueChange}
        trackColor={{ false: '#767577', true: '#4CAF50' }}
        thumbColor={value ? '#fff' : '#f4f3f4'}
        accessibilityLabel={label}
      />
    </View>
  );

  return (
    <ScrollView style={styles.container} accessibilityRole="scrollview">
      {/* Language Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle} accessibilityRole="header">
          {t('settings.language')}
        </Text>
        <View style={styles.languageList}>
          {getAvailableLanguages().map(renderLanguageOption)}
        </View>
      </View>

      {/* Accessibility Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle} accessibilityRole="header">
          {t('settings.accessibility')}
        </Text>
        {renderSettingItem(
          'High Contrast Mode',
          highContrast,
          setHighContrast,
          'Increase contrast for better visibility'
        )}
        {renderSettingItem(
          'Large Text',
          largeText,
          setLargeText,
          'Increase font size throughout the app'
        )}
        {renderSettingItem(
          'Screen Reader Support',
          screenReader,
          setScreenReader,
          'Optimize for screen readers like TalkBack/VoiceOver'
        )}
        {renderSettingItem(
          'Voice Commands',
          voiceCommands,
          setVoiceCommands,
          'Control the app with voice commands'
        )}
        {renderSettingItem(
          'Color Blind Mode',
          colorBlindMode,
          setColorBlindMode,
          'Use color-blind friendly color schemes'
        )}
      </View>

      {/* Notifications Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle} accessibilityRole="header">
          {t('settings.notifications')}
        </Text>
        {renderSettingItem(
          'Expiration Alerts',
          expirationAlerts,
          setExpirationAlerts,
          'Get notified when items are about to expire'
        )}
        {renderSettingItem(
          'Achievement Notifications',
          achievementNotifications,
          setAchievementNotifications,
          'Receive notifications for unlocked achievements'
        )}
        {renderSettingItem(
          'Daily Summary',
          dailySummary,
          setDailySummary,
          'Get a daily summary of your inventory'
        )}
      </View>

      {/* Nutrition & Health Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle} accessibilityRole="header">
          Nutrition & Health
        </Text>
        <TouchableOpacity
          style={styles.menuItem}
          onPress={() => navigation.navigate('DietaryPreferences')}
          accessibilityRole="button"
        >
          <Text style={styles.menuItemText}>Dietary Preferences</Text>
          <Text style={styles.menuItemArrow}>›</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.menuItem}
          onPress={() => navigation.navigate('AllergenManagement')}
          accessibilityRole="button"
        >
          <Text style={styles.menuItemText}>Manage Allergens</Text>
          <Text style={styles.menuItemArrow}>›</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.menuItem}
          onPress={() => navigation.navigate('AllergenAlerts')}
          accessibilityRole="button"
        >
          <Text style={styles.menuItemText}>⚠️ Allergen Alerts</Text>
          <Text style={styles.menuItemArrow}>›</Text>
        </TouchableOpacity>
      </View>

      {/* Child Mode Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle} accessibilityRole="header">
          Child-Friendly Mode
        </Text>
        {renderSettingItem(
          'Enable Child Mode',
          childMode,
          setChildMode,
          'Simplified interface with larger buttons and fun animations'
        )}
        {renderSettingItem(
          'Parental Controls',
          parentalControls,
          setParentalControls,
          'Require PIN for certain actions'
        )}
      </View>

      {/* About Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle} accessibilityRole="header">
          {t('settings.about')}
        </Text>
        <View style={styles.aboutInfo}>
          <Text style={styles.aboutText}>
            {t('app.name')}
          </Text>
          <Text style={styles.aboutVersion}>Version 1.0.0</Text>
          <Text style={styles.aboutTagline}>{t('app.tagline')}</Text>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  section: {
    backgroundColor: '#fff',
    marginTop: 20,
    paddingVertical: 10,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    paddingHorizontal: 20,
    paddingVertical: 10,
    backgroundColor: '#f9f9f9',
  },
  languageList: {
    paddingVertical: 5,
  },
  languageOption: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  selectedLanguage: {
    backgroundColor: '#e8f5e9',
  },
  languageInfo: {
    flex: 1,
  },
  languageName: {
    fontSize: 16,
    color: '#333',
    fontWeight: '500',
  },
  languageNative: {
    fontSize: 14,
    color: '#666',
    marginTop: 2,
  },
  checkmark: {
    fontSize: 20,
    color: '#4CAF50',
    fontWeight: 'bold',
  },
  settingItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  settingInfo: {
    flex: 1,
    marginRight: 15,
  },
  settingLabel: {
    fontSize: 16,
    color: '#333',
    fontWeight: '500',
  },
  settingDescription: {
    fontSize: 13,
    color: '#666',
    marginTop: 4,
  },
  aboutInfo: {
    paddingHorizontal: 20,
    paddingVertical: 20,
    alignItems: 'center',
  },
  aboutText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    textAlign: 'center',
  },
  aboutVersion: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  aboutTagline: {
    fontSize: 14,
    color: '#4CAF50',
    marginTop: 10,
    textAlign: 'center',
    fontStyle: 'italic',
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  menuItemText: {
    fontSize: 16,
    color: '#333',
    fontWeight: '500',
  },
  menuItemArrow: {
    fontSize: 24,
    color: '#999',
  },
});

export default SettingsScreen;
