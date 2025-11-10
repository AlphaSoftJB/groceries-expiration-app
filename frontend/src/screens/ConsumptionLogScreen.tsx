import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  TextInput,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { useQuery, useMutation, gql } from '@apollo/client';

// GraphQL Operations
const GET_HOUSEHOLD_ITEMS = gql\`
  query GetHouseholdItems {
    items {
      id
      name
      quantity
      expirationDate
    }
  }
\`;

const LOG_CONSUMPTION = gql\`
  mutation LogConsumption($input: ConsumptionLogInput!) {
    logConsumption(input: $input) {
      id
      item {
        name
      }
      servingsConsumed
      totalCalories
      mealType
      consumedAt
    }
  }
\`;

const GET_DAILY_SUMMARY = gql\`
  query GetDailySummary($date: String!) {
    getDailyNutritionSummary(date: $date) {
      date
      totalCalories
      totalProtein
      totalCarbs
      totalFat
      mealsLogged
    }
  }
\`;

enum MealType {
  BREAKFAST = 'BREAKFAST',
  LUNCH = 'LUNCH',
  DINNER = 'DINNER',
  SNACK = 'SNACK',
}

const ConsumptionLogScreen: React.FC = () => {
  const [selectedItemId, setSelectedItemId] = useState<string>('');
  const [servings, setServings] = useState('1');
  const [selectedMeal, setSelectedMeal] = useState<MealType>(MealType.BREAKFAST);
  const today = new Date().toISOString().split('T')[0];

  const { data: itemsData, loading: itemsLoading } = useQuery(GET_HOUSEHOLD_ITEMS);
  const { data: summaryData, loading: summaryLoading, refetch: refetchSummary } = useQuery(
    GET_DAILY_SUMMARY,
    {
      variables: { date: today },
    }
  );

  const [logConsumption, { loading: logLoading }] = useMutation(LOG_CONSUMPTION, {
    onCompleted: (data) => {
      Alert.alert(
        'Success',
        \`Logged \${data.logConsumption.servingsConsumed} serving(s) of \${data.logConsumption.item.name}\`
      );
      setSelectedItemId('');
      setServings('1');
      refetchSummary();
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  const handleLogConsumption = () => {
    if (!selectedItemId) {
      Alert.alert('Error', 'Please select an item');
      return;
    }

    const servingsNum = parseFloat(servings);
    if (isNaN(servingsNum) || servingsNum <= 0) {
      Alert.alert('Error', 'Please enter a valid number of servings');
      return;
    }

    logConsumption({
      variables: {
        input: {
          itemId: selectedItemId,
          servingsConsumed: servingsNum,
          mealType: selectedMeal,
          consumedAt: new Date().toISOString(),
        },
      },
    });
  };

  const items = itemsData?.items || [];
  const summary = summaryData?.getDailyNutritionSummary;

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Log Your Meal</Text>

      {/* Daily Summary Card */}
      {summaryLoading ? (
        <ActivityIndicator size="large" color="#4CAF50" />
      ) : summary ? (
        <View style={styles.summaryCard}>
          <Text style={styles.summaryTitle}>Today's Nutrition</Text>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Calories:</Text>
            <Text style={styles.summaryValue}>{summary.totalCalories || 0} kcal</Text>
          </View>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Protein:</Text>
            <Text style={styles.summaryValue}>{summary.totalProtein || 0}g</Text>
          </View>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Carbs:</Text>
            <Text style={styles.summaryValue}>{summary.totalCarbs || 0}g</Text>
          </View>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Fat:</Text>
            <Text style={styles.summaryValue}>{summary.totalFat || 0}g</Text>
          </View>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Meals Logged:</Text>
            <Text style={styles.summaryValue}>{summary.mealsLogged || 0}</Text>
          </View>
        </View>
      ) : (
        <View style={styles.summaryCard}>
          <Text style={styles.noDataText}>No meals logged today. Start tracking!</Text>
        </View>
      )}

      {/* Meal Type Selection */}
      <Text style={styles.label}>Meal Type</Text>
      <View style={styles.mealTypeContainer}>
        {Object.values(MealType).map((meal) => (
          <TouchableOpacity
            key={meal}
            style={[styles.mealButton, selectedMeal === meal && styles.mealButtonSelected]}
            onPress={() => setSelectedMeal(meal)}
          >
            <Text
              style={[styles.mealButtonText, selectedMeal === meal && styles.mealButtonTextSelected]}
            >
              {meal.charAt(0) + meal.slice(1).toLowerCase()}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Item Selection */}
      <Text style={styles.label}>Select Item</Text>
      {itemsLoading ? (
        <ActivityIndicator size="small" color="#4CAF50" />
      ) : (
        <ScrollView style={styles.itemsList} nestedScrollEnabled>
          {items.map((item: any) => (
            <TouchableOpacity
              key={item.id}
              style={[
                styles.itemCard,
                selectedItemId === item.id && styles.itemCardSelected,
              ]}
              onPress={() => setSelectedItemId(item.id)}
            >
              <Text style={styles.itemName}>{item.name}</Text>
              <Text style={styles.itemDetails}>
                Qty: {item.quantity} | Exp: {item.expirationDate}
              </Text>
            </TouchableOpacity>
          ))}
        </ScrollView>
      )}

      {/* Servings Input */}
      <Text style={styles.label}>Number of Servings</Text>
      <TextInput
        style={styles.input}
        value={servings}
        onChangeText={setServings}
        keyboardType="decimal-pad"
        placeholder="1.0"
      />

      {/* Log Button */}
      <TouchableOpacity
        style={[styles.logButton, logLoading && styles.logButtonDisabled]}
        onPress={handleLogConsumption}
        disabled={logLoading}
      >
        <Text style={styles.logButtonText}>
          {logLoading ? 'Logging...' : 'Log Consumption'}
        </Text>
      </TouchableOpacity>

      <View style={styles.bottomPadding} />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    padding: 16,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 20,
    color: '#333',
  },
  summaryCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 24,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  summaryTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
    color: '#4CAF50',
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 6,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  summaryLabel: {
    fontSize: 16,
    color: '#666',
  },
  summaryValue: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
  },
  noDataText: {
    fontSize: 16,
    color: '#999',
    textAlign: 'center',
    fontStyle: 'italic',
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    marginTop: 16,
    marginBottom: 8,
    color: '#333',
  },
  mealTypeContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 16,
  },
  mealButton: {
    backgroundColor: '#fff',
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 20,
    marginRight: 8,
    marginBottom: 8,
    borderWidth: 2,
    borderColor: '#ddd',
  },
  mealButtonSelected: {
    backgroundColor: '#4CAF50',
    borderColor: '#4CAF50',
  },
  mealButtonText: {
    fontSize: 14,
    color: '#666',
    fontWeight: '600',
  },
  mealButtonTextSelected: {
    color: '#fff',
  },
  itemsList: {
    maxHeight: 300,
    marginBottom: 16,
  },
  itemCard: {
    backgroundColor: '#fff',
    padding: 12,
    borderRadius: 8,
    marginBottom: 8,
    borderWidth: 2,
    borderColor: '#f0f0f0',
  },
  itemCardSelected: {
    borderColor: '#4CAF50',
    backgroundColor: '#f1f8f4',
  },
  itemName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  itemDetails: {
    fontSize: 12,
    color: '#666',
  },
  input: {
    backgroundColor: '#fff',
    padding: 12,
    borderRadius: 8,
    fontSize: 16,
    borderWidth: 1,
    borderColor: '#ddd',
    marginBottom: 16,
  },
  logButton: {
    backgroundColor: '#4CAF50',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 8,
  },
  logButtonDisabled: {
    backgroundColor: '#ccc',
  },
  logButtonText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
  },
  bottomPadding: {
    height: 40,
  },
});

export default ConsumptionLogScreen;
