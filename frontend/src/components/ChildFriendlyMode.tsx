import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Animated,
  Image,
  Alert,
} from 'react-native';
import { useTranslation } from 'react-i18next';
import AsyncStorage from '@react-native-async-storage/async-storage';

/**
 * Child-Friendly Mode Component
 * Features:
 * - Simplified UI with larger buttons
 * - Fun animations and colorful design
 * - Educational content about food and sustainability
 * - Parental controls with PIN protection
 * - Age-appropriate content filtering
 * - Gamification with kid-friendly rewards
 */

const PARENTAL_PIN_KEY = '@groceries_app_parental_pin';
const CHILD_MODE_KEY = '@groceries_app_child_mode';

interface ChildFriendlyModeProps {
  enabled: boolean;
  onToggle: (enabled: boolean) => void;
}

export const ChildFriendlyMode: React.FC<ChildFriendlyModeProps> = ({
  enabled,
  onToggle,
}) => {
  const { t } = useTranslation();
  const [parentalPin, setParentalPin] = useState<string | null>(null);
  const [bounceAnim] = useState(new Animated.Value(0));

  useEffect(() => {
    loadParentalPin();
    if (enabled) {
      startBounceAnimation();
    }
  }, [enabled]);

  const loadParentalPin = async () => {
    try {
      const pin = await AsyncStorage.getItem(PARENTAL_PIN_KEY);
      setParentalPin(pin);
    } catch (error) {
      console.error('Error loading parental PIN:', error);
    }
  };

  const startBounceAnimation = () => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(bounceAnim, {
          toValue: 1,
          duration: 500,
          useNativeDriver: true,
        }),
        Animated.timing(bounceAnim, {
          toValue: 0,
          duration: 500,
          useNativeDriver: true,
        }),
      ])
    ).start();
  };

  const bounceInterpolate = bounceAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [0, -10],
  });

  const handleToggle = async () => {
    if (enabled && parentalPin) {
      // Require PIN to disable child mode
      Alert.prompt(
        'Parental Control',
        'Enter PIN to disable child mode',
        [
          {
            text: 'Cancel',
            style: 'cancel',
          },
          {
            text: 'OK',
            onPress: async (pin) => {
              if (pin === parentalPin) {
                await AsyncStorage.setItem(CHILD_MODE_KEY, 'false');
                onToggle(false);
              } else {
                Alert.alert('Error', 'Incorrect PIN');
              }
            },
          },
        ],
        'secure-text'
      );
    } else {
      // Enable child mode
      await AsyncStorage.setItem(CHILD_MODE_KEY, 'true');
      onToggle(true);
    }
  };

  const handleSetPin = () => {
    Alert.prompt(
      'Set Parental PIN',
      'Enter a 4-digit PIN',
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'OK',
          onPress: async (pin) => {
            if (pin && pin.length === 4 && /^\d+$/.test(pin)) {
              await AsyncStorage.setItem(PARENTAL_PIN_KEY, pin);
              setParentalPin(pin);
              Alert.alert('Success', 'Parental PIN set successfully');
            } else {
              Alert.alert('Error', 'PIN must be 4 digits');
            }
          },
        },
      ],
      'secure-text'
    );
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={[styles.toggleButton, enabled && styles.toggleButtonActive]}
        onPress={handleToggle}
        accessibilityLabel="Toggle child-friendly mode"
        accessibilityRole="button"
      >
        <Animated.View
          style={[
            styles.iconContainer,
            { transform: [{ translateY: bounceInterpolate }] },
          ]}
        >
          <Text style={styles.icon}>{enabled ? '🎈' : '👶'}</Text>
        </Animated.View>
        <Text style={styles.toggleText}>
          {enabled ? 'Child Mode: ON' : 'Child Mode: OFF'}
        </Text>
      </TouchableOpacity>

      {!parentalPin && (
        <TouchableOpacity
          style={styles.setPinButton}
          onPress={handleSetPin}
          accessibilityLabel="Set parental PIN"
          accessibilityRole="button"
        >
          <Text style={styles.setPinText}>🔒 Set Parental PIN</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};

/**
 * Child-Friendly Item Card
 * Larger, more colorful item cards for kids
 */
interface ChildFriendlyItemCardProps {
  item: {
    name: string;
    quantity: number;
    expirationDate: string;
    storageLocation: string;
  };
  onPress: () => void;
}

export const ChildFriendlyItemCard: React.FC<ChildFriendlyItemCardProps> = ({
  item,
  onPress,
}) => {
  const [scaleAnim] = useState(new Animated.Value(1));

  const handlePressIn = () => {
    Animated.spring(scaleAnim, {
      toValue: 0.95,
      useNativeDriver: true,
    }).start();
  };

  const handlePressOut = () => {
    Animated.spring(scaleAnim, {
      toValue: 1,
      friction: 3,
      tension: 40,
      useNativeDriver: true,
    }).start();
  };

  const getStorageEmoji = (location: string) => {
    switch (location.toLowerCase()) {
      case 'fridge':
        return '🧊';
      case 'freezer':
        return '❄️';
      case 'pantry':
        return '🏠';
      case 'counter':
        return '🍽️';
      default:
        return '📦';
    }
  };

  return (
    <Animated.View style={[styles.card, { transform: [{ scale: scaleAnim }] }]}>
      <TouchableOpacity
        onPress={onPress}
        onPressIn={handlePressIn}
        onPressOut={handlePressOut}
        activeOpacity={0.8}
        accessibilityLabel={`${item.name}, ${item.quantity} items`}
        accessibilityRole="button"
      >
        <View style={styles.cardContent}>
          <Text style={styles.cardEmoji}>{getStorageEmoji(item.storageLocation)}</Text>
          <View style={styles.cardInfo}>
            <Text style={styles.cardTitle}>{item.name}</Text>
            <Text style={styles.cardQuantity}>🔢 {item.quantity}</Text>
            <Text style={styles.cardExpiration}>📅 {item.expirationDate}</Text>
          </View>
          <Text style={styles.cardArrow}>➡️</Text>
        </View>
      </TouchableOpacity>
    </Animated.View>
  );
};

/**
 * Educational Fun Fact Component
 * Teaches kids about food and sustainability
 */
export const EducationalFunFact: React.FC = () => {
  const funFacts = [
    '🌍 Did you know? Saving food helps save the planet!',
    '🍎 Fresh fruits and veggies give you superpowers!',
    '♻️ When we don\'t waste food, we help the Earth smile!',
    '🌱 Every food item saved is a tree planted!',
    '🦸 You\'re a Food Hero when you don\'t waste!',
    '🌈 Colorful foods make you strong and healthy!',
    '🐝 Bees help grow our food. Let\'s help them back!',
    '💧 Saving food also saves water!',
  ];

  const [currentFact, setCurrentFact] = useState(
    funFacts[Math.floor(Math.random() * funFacts.length)]
  );

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentFact(funFacts[Math.floor(Math.random() * funFacts.length)]);
    }, 10000); // Change fact every 10 seconds

    return () => clearInterval(interval);
  }, []);

  return (
    <View style={styles.funFactContainer}>
      <Text style={styles.funFactText}>{currentFact}</Text>
    </View>
  );
};

/**
 * Kid-Friendly Achievement Badge
 */
interface KidAchievementBadgeProps {
  achievement: {
    name: string;
    icon: string;
    unlocked: boolean;
  };
}

export const KidAchievementBadge: React.FC<KidAchievementBadgeProps> = ({
  achievement,
}) => {
  const [rotateAnim] = useState(new Animated.Value(0));

  useEffect(() => {
    if (achievement.unlocked) {
      Animated.loop(
        Animated.timing(rotateAnim, {
          toValue: 1,
          duration: 2000,
          useNativeDriver: true,
        })
      ).start();
    }
  }, [achievement.unlocked]);

  const rotate = rotateAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ['0deg', '360deg'],
  });

  return (
    <Animated.View
      style={[
        styles.badge,
        achievement.unlocked && styles.badgeUnlocked,
        achievement.unlocked && { transform: [{ rotate }] },
      ]}
    >
      <Text style={styles.badgeIcon}>{achievement.icon}</Text>
      <Text style={styles.badgeName}>{achievement.name}</Text>
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
  },
  toggleButton: {
    backgroundColor: '#E0E0E0',
    borderRadius: 20,
    padding: 20,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  toggleButtonActive: {
    backgroundColor: '#FFD700',
  },
  iconContainer: {
    marginRight: 10,
  },
  icon: {
    fontSize: 40,
  },
  toggleText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  setPinButton: {
    marginTop: 15,
    backgroundColor: '#FF6B6B',
    borderRadius: 15,
    padding: 15,
    alignItems: 'center',
  },
  setPinText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFF',
  },
  card: {
    backgroundColor: '#FFF',
    borderRadius: 20,
    padding: 20,
    marginVertical: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 5,
    borderWidth: 3,
    borderColor: '#FFD700',
  },
  cardContent: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  cardEmoji: {
    fontSize: 50,
    marginRight: 15,
  },
  cardInfo: {
    flex: 1,
  },
  cardTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 5,
  },
  cardQuantity: {
    fontSize: 18,
    color: '#666',
    marginBottom: 3,
  },
  cardExpiration: {
    fontSize: 18,
    color: '#666',
  },
  cardArrow: {
    fontSize: 30,
  },
  funFactContainer: {
    backgroundColor: '#E8F5E9',
    borderRadius: 15,
    padding: 20,
    marginVertical: 15,
    borderWidth: 2,
    borderColor: '#4CAF50',
  },
  funFactText: {
    fontSize: 18,
    color: '#2E7D32',
    textAlign: 'center',
    fontWeight: '600',
  },
  badge: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: '#E0E0E0',
    justifyContent: 'center',
    alignItems: 'center',
    margin: 10,
    borderWidth: 3,
    borderColor: '#BDBDBD',
  },
  badgeUnlocked: {
    backgroundColor: '#FFD700',
    borderColor: '#FFA000',
  },
  badgeIcon: {
    fontSize: 40,
  },
  badgeName: {
    fontSize: 12,
    fontWeight: 'bold',
    color: '#333',
    marginTop: 5,
    textAlign: 'center',
  },
});

export default ChildFriendlyMode;
