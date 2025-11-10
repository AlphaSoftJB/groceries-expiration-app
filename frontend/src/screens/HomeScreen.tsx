import React from 'react';
import { View, Text, StyleSheet, Button } from 'react-native';
import { useQuery, gql } from '@apollo/client';
import { ScrollView, Button } from 'react-native';
import ItemCard from '../components/ItemCard';
import { ScrollView } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

// Define the GraphQL Query
const ITEMS_QUERY = gql\`
  query ItemsQuery($householdId: ID!) {
    itemsByHousehold(householdId: $householdId) {
      id
      name
      quantity
      expirationDate
      predictedExpirationDate
      storageLocation
    }
  }
\`;

const HELLO_QUERY = gql\`
  query HelloQuery {
    hello
  }
\`;

// Define the navigation stack types
type RootStackParamList = {
  Home: undefined;
  AddItem: undefined;
  ARView: undefined;
  ImpactDashboard: undefined;
  SmartAppliance: undefined;
  ShoppingList: undefined;
  ItemDetail: { itemId: string };
  Settings: undefined;
  AllergenManagement: undefined;
  ConsumptionLog: undefined;
  DietaryPreferences: undefined;
  AllergenAlerts: undefined;
  RecipeSuggestions: { householdId: string };
  RecipeDetail: { recipeId: string };
};

type HomeScreenProps = NativeStackScreenProps<RootStackParamList, 'Home'>;

const HomeScreen: React.FC<HomeScreenProps> = ({ navigation }) => {
  // Hardcoded householdId for now, will be dynamic after auth implementation
  const HOUSEHOLD_ID = "1"; 

  const { loading, error, data } = useQuery(ITEMS_QUERY, {
    variables: { householdId: HOUSEHOLD_ID },
    fetchPolicy: 'cache-and-network',
  });

  const { data: helloData } = useQuery(HELLO_QUERY);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome to My Fridge!</Text>
      
      <View style={styles.backendStatus}>
        <Text style={styles.subtitle}>Backend Status (GraphQL):</Text>
        {loading && <Text>Loading Items...</Text>}
        {error && <Text style={styles.errorText}>Error fetching items: {error.message}</Text>}
        {helloData && <Text style={styles.successText}>Hello Query Success: "{helloData.hello}"</Text>}
      </View>

      <Text style={styles.listTitle}>Household Items (ID: {HOUSEHOLD_ID})</Text>
      <ScrollView style={styles.listContainer}>
        {data?.itemsByHousehold.map((item: any) => (
          <ItemCard
            key={item.id}
            item={item}
            onPress={() => navigation.navigate('ItemDetail', { itemId: item.id })}
          />
        ))}
        {data?.itemsByHousehold.length === 0 && !loading && (
          <Text style={styles.emptyListText}>No items found. Add some items to your household!</Text>
        )}
      </ScrollView>

      <Button
        title="Add New Item"
        onPress={() => navigation.navigate('AddItem')}
      />
      <Button
        title="AR Fridge View"
        onPress={() => navigation.navigate('ARView')}
      />
      <Button
        title="Impact Dashboard"
        onPress={() => navigation.navigate('ImpactDashboard')}
      />
      <Button
        title="Smart Appliance Sync"
        onPress={() => navigation.navigate('SmartAppliance')}
      />
      <Button
        title="Shopping List"
        onPress={() => navigation.navigate('ShoppingList')}
      />
      <Button
        title="🍳 Recipe Suggestions"
        onPress={() => navigation.navigate('RecipeSuggestions', { householdId: HOUSEHOLD_ID })}
        color="#FF9800"
      />
      <Button
        title="⚠️ Allergen Alerts"
        onPress={() => navigation.navigate('AllergenAlerts')}
        color="#F44336"
      />
      <Button
        title="🍽️ Log Meal"
        onPress={() => navigation.navigate('ConsumptionLog')}
        color="#4CAF50"
      />
      <Button
        title="⚙️ Settings"
        onPress={() => navigation.navigate('Settings')}
      />
      
      <Text style={styles.placeholderText}>
        This screen will be the main dashboard, displaying expiring items, CO₂ impact, and quick action buttons (AR, Scan, Shopping List).
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 18,
    fontWeight: '600',
    marginTop: 10,
  },
  backendStatus: {
    padding: 15,
    backgroundColor: '#fff',
    borderRadius: 8,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  listTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  listContainer: {
    flex: 1,
  },
  emptyListText: {
    textAlign: 'center',
    marginTop: 20,
    color: '#999',
  },
  errorText: {
    color: 'red',
    marginTop: 5,
  },
  successText: {
    color: 'green',
    marginTop: 5,
  },
  placeholderText: {
    marginTop: 30,
    fontStyle: 'italic',
    textAlign: 'center',
    color: '#666',
  }
});

export default HomeScreen;
