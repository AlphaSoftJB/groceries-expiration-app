import React from 'react';
import { ApolloProvider } from '@apollo/client';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import apolloClient from './src/services/apolloClient';
import HomeScreen from './src/screens/HomeScreen';
import AddItemScreen from './src/screens/AddItemScreen';
import ARViewScreen from './src/screens/ARViewScreen';
import ImpactDashboardScreen from './src/screens/ImpactDashboardScreen';
import SmartApplianceScreen from './src/screens/SmartApplianceScreen';
import ShoppingListScreen from './src/screens/ShoppingListScreen';
import ItemDetailScreen from './src/screens/ItemDetailScreen';
import BarcodeScannerScreen from './src/screens/BarcodeScannerScreen';
import CameraScreen from './src/screens/CameraScreen';
import NotificationSettingsScreen from './src/screens/NotificationSettingsScreen';
import AllergenManagementScreen from './src/screens/AllergenManagementScreen';
import ConsumptionLogScreen from './src/screens/ConsumptionLogScreen';
import DietaryPreferencesScreen from './src/screens/DietaryPreferencesScreen';
import AllergenAlertsScreen from './src/screens/AllergenAlertsScreen';
import RecipeSuggestionsScreen from './src/screens/RecipeSuggestionsScreen';
import RecipeDetailScreen from './src/screens/RecipeDetailScreen';
import SettingsScreen from './src/screens/SettingsScreen';
import { Text, View, StyleSheet } from 'react-native';

// Define the Stack Navigator
const Stack = createNativeStackNavigator();

const AppContent = () => {
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Home">
          <Stack.Screen name="Home" component={HomeScreen} options={{ title: 'My Fridge' }} />
          <Stack.Screen name="AddItem" component={AddItemScreen} options={{ title: 'Add New Item' }} />
          <Stack.Screen name="ARView" component={ARViewScreen} options={{ title: 'AR Fridge View' }} />
          <Stack.Screen name="ImpactDashboard" component={ImpactDashboardScreen} options={{ title: 'Impact Dashboard' }} />
          <Stack.Screen name="SmartAppliance" component={SmartApplianceScreen} options={{ title: 'Smart Appliance Sync' }} />
          <Stack.Screen name="ShoppingList" component={ShoppingListScreen} options={{ title: 'Shopping List' }} />
          <Stack.Screen name="ItemDetail" component={ItemDetailScreen} options={{ title: 'Item Details' }} />
          <Stack.Screen name="BarcodeScanner" component={BarcodeScannerScreen} options={{ title: 'Scan Barcode' }} />
          <Stack.Screen name="Camera" component={CameraScreen} options={{ title: 'Camera OCR' }} />
          <Stack.Screen name="NotificationSettings" component={NotificationSettingsScreen} options={{ title: 'Notifications' }} />
          <Stack.Screen name="Settings" component={SettingsScreen} options={{ title: 'Settings' }} />
          <Stack.Screen name="AllergenManagement" component={AllergenManagementScreen} options={{ title: 'Manage Allergens' }} />
          <Stack.Screen name="ConsumptionLog" component={ConsumptionLogScreen} options={{ title: 'Log Meal' }} />
          <Stack.Screen name="DietaryPreferences" component={DietaryPreferencesScreen} options={{ title: 'Dietary Preferences' }} />
          <Stack.Screen name="AllergenAlerts" component={AllergenAlertsScreen} options={{ title: 'Allergen Alerts' }} />
          <Stack.Screen name="RecipeSuggestions" component={RecipeSuggestionsScreen} options={{ title: 'Recipe Suggestions' }} />
          <Stack.Screen name="RecipeDetail" component={RecipeDetailScreen} options={{ title: 'Recipe Details' }} />
        </Stack.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
};

const App = () => {
  return (
    <ApolloProvider client={apolloClient}>
      <AppContent />
    </ApolloProvider>
  );
};

export default App;
