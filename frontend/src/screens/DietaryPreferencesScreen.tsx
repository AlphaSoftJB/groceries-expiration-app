import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Switch,
  TextInput,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { useQuery, useMutation, gql } from '@apollo/client';

// GraphQL Operations
const GET_DIETARY_PREFERENCES = gql\`
  query GetDietaryPreferences {
    getUserDietaryPreferences {
      id
      isVegan
      isVegetarian
      isPescatarian
      isKeto
      isPaleo
      isGlutenFree
      isLactoseFree
      isNutFree
      isLowCarb
      isLowFat
      dailyCalorieGoal
      dailyProteinGoal
      dailyCarbGoal
      dailyFatGoal
    }
  }
\`;

const UPDATE_DIETARY_PREFERENCES = gql\`
  mutation UpdateDietaryPreferences($input: DietaryPreferencesInput!) {
    updateDietaryPreferences(input: $input) {
      id
      isVegan
      isVegetarian
      dailyCalorieGoal
      dailyProteinGoal
      dailyCarbGoal
      dailyFatGoal
    }
  }
\`;

interface DietaryPreferences {
  isVegan: boolean;
  isVegetarian: boolean;
  isPescatarian: boolean;
  isKeto: boolean;
  isPaleo: boolean;
  isGlutenFree: boolean;
  isLactoseFree: boolean;
  isNutFree: boolean;
  isLowCarb: boolean;
  isLowFat: boolean;
  dailyCalorieGoal: number;
  dailyProteinGoal: number;
  dailyCarbGoal: number;
  dailyFatGoal: number;
}

const DietaryPreferencesScreen: React.FC = () => {
  const [preferences, setPreferences] = useState<DietaryPreferences>({
    isVegan: false,
    isVegetarian: false,
    isPescatarian: false,
    isKeto: false,
    isPaleo: false,
    isGlutenFree: false,
    isLactoseFree: false,
    isNutFree: false,
    isLowCarb: false,
    isLowFat: false,
    dailyCalorieGoal: 2000,
    dailyProteinGoal: 50,
    dailyCarbGoal: 250,
    dailyFatGoal: 70,
  });

  const { data, loading: queryLoading } = useQuery(GET_DIETARY_PREFERENCES);

  const [updatePreferences, { loading: updateLoading }] = useMutation(UPDATE_DIETARY_PREFERENCES, {
    onCompleted: () => {
      Alert.alert('Success', 'Dietary preferences updated successfully!');
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  useEffect(() => {
    if (data?.getUserDietaryPreferences) {
      setPreferences(data.getUserDietaryPreferences);
    }
  }, [data]);

  const handleToggle = (key: keyof DietaryPreferences) => {
    setPreferences((prev) => ({
      ...prev,
      [key]: !prev[key],
    }));
  };

  const handleNumberChange = (key: keyof DietaryPreferences, value: string) => {
    const numValue = parseFloat(value);
    if (!isNaN(numValue) && numValue >= 0) {
      setPreferences((prev) => ({
        ...prev,
        [key]: numValue,
      }));
    }
  };

  const handleSave = () => {
    updatePreferences({
      variables: {
        input: preferences,
      },
    });
  };

  if (queryLoading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#4CAF50" />
        <Text style={styles.loadingText}>Loading preferences...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Dietary Preferences</Text>
      <Text style={styles.subtitle}>
        Set your dietary restrictions and daily nutrition goals
      </Text>

      {/* Dietary Restrictions */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Dietary Restrictions</Text>

        <PreferenceRow
          label="Vegan"
          description="No animal products"
          value={preferences.isVegan}
          onToggle={() => handleToggle('isVegan')}
        />
        <PreferenceRow
          label="Vegetarian"
          description="No meat or fish"
          value={preferences.isVegetarian}
          onToggle={() => handleToggle('isVegetarian')}
        />
        <PreferenceRow
          label="Pescatarian"
          description="No meat, fish allowed"
          value={preferences.isPescatarian}
          onToggle={() => handleToggle('isPescatarian')}
        />
        <PreferenceRow
          label="Keto"
          description="Low-carb, high-fat"
          value={preferences.isKeto}
          onToggle={() => handleToggle('isKeto')}
        />
        <PreferenceRow
          label="Paleo"
          description="Whole foods only"
          value={preferences.isPaleo}
          onToggle={() => handleToggle('isPaleo')}
        />
        <PreferenceRow
          label="Gluten-Free"
          description="No gluten"
          value={preferences.isGlutenFree}
          onToggle={() => handleToggle('isGlutenFree')}
        />
        <PreferenceRow
          label="Lactose-Free"
          description="No dairy"
          value={preferences.isLactoseFree}
          onToggle={() => handleToggle('isLactoseFree')}
        />
        <PreferenceRow
          label="Nut-Free"
          description="No nuts"
          value={preferences.isNutFree}
          onToggle={() => handleToggle('isNutFree')}
        />
        <PreferenceRow
          label="Low-Carb"
          description="Reduced carbohydrates"
          value={preferences.isLowCarb}
          onToggle={() => handleToggle('isLowCarb')}
        />
        <PreferenceRow
          label="Low-Fat"
          description="Reduced fat intake"
          value={preferences.isLowFat}
          onToggle={() => handleToggle('isLowFat')}
        />
      </View>

      {/* Daily Nutrition Goals */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Daily Nutrition Goals</Text>

        <View style={styles.goalRow}>
          <Text style={styles.goalLabel}>Calories (kcal)</Text>
          <TextInput
            style={styles.goalInput}
            value={String(preferences.dailyCalorieGoal)}
            onChangeText={(value) => handleNumberChange('dailyCalorieGoal', value)}
            keyboardType="numeric"
          />
        </View>

        <View style={styles.goalRow}>
          <Text style={styles.goalLabel}>Protein (g)</Text>
          <TextInput
            style={styles.goalInput}
            value={String(preferences.dailyProteinGoal)}
            onChangeText={(value) => handleNumberChange('dailyProteinGoal', value)}
            keyboardType="numeric"
          />
        </View>

        <View style={styles.goalRow}>
          <Text style={styles.goalLabel}>Carbohydrates (g)</Text>
          <TextInput
            style={styles.goalInput}
            value={String(preferences.dailyCarbGoal)}
            onChangeText={(value) => handleNumberChange('dailyCarbGoal', value)}
            keyboardType="numeric"
          />
        </View>

        <View style={styles.goalRow}>
          <Text style={styles.goalLabel}>Fat (g)</Text>
          <TextInput
            style={styles.goalInput}
            value={String(preferences.dailyFatGoal)}
            onChangeText={(value) => handleNumberChange('dailyFatGoal', value)}
            keyboardType="numeric"
          />
        </View>
      </View>

      {/* Save Button */}
      <TouchableOpacity
        style={[styles.saveButton, updateLoading && styles.saveButtonDisabled]}
        onPress={handleSave}
        disabled={updateLoading}
      >
        <Text style={styles.saveButtonText}>
          {updateLoading ? 'Saving...' : 'Save Preferences'}
        </Text>
      </TouchableOpacity>

      <View style={styles.bottomPadding} />
    </ScrollView>
  );
};

// Helper component for preference rows
interface PreferenceRowProps {
  label: string;
  description: string;
  value: boolean;
  onToggle: () => void;
}

const PreferenceRow: React.FC<PreferenceRowProps> = ({ label, description, value, onToggle }) => (
  <View style={styles.preferenceRow}>
    <View style={styles.preferenceInfo}>
      <Text style={styles.preferenceLabel}>{label}</Text>
      <Text style={styles.preferenceDescription}>{description}</Text>
    </View>
    <Switch
      value={value}
      onValueChange={onToggle}
      trackColor={{ false: '#ccc', true: '#81C784' }}
      thumbColor={value ? '#4CAF50' : '#f4f3f4'}
    />
  </View>
);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    padding: 16,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: '#666',
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#333',
  },
  subtitle: {
    fontSize: 14,
    color: '#666',
    marginBottom: 24,
  },
  section: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#4CAF50',
  },
  preferenceRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  preferenceInfo: {
    flex: 1,
    marginRight: 12,
  },
  preferenceLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 2,
  },
  preferenceDescription: {
    fontSize: 12,
    color: '#999',
  },
  goalRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  goalLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    flex: 1,
  },
  goalInput: {
    backgroundColor: '#f9f9f9',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 8,
    fontSize: 16,
    width: 100,
    textAlign: 'right',
  },
  saveButton: {
    backgroundColor: '#4CAF50',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 8,
  },
  saveButtonDisabled: {
    backgroundColor: '#ccc',
  },
  saveButtonText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
  },
  bottomPadding: {
    height: 40,
  },
});

export default DietaryPreferencesScreen;
