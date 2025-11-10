import React, { useState } from 'react';
import { View, Text, StyleSheet, Button, Alert, ScrollView, TextInput } from 'react-native';
import { useMutation, gql } from '@apollo/client';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

// Define the GraphQL Mutation
const SYNC_APPLIANCE_MUTATION = gql\`
  mutation SyncAppliance($input: SyncApplianceInput!) {
    syncApplianceData(input: $input)
  }
\`;

// Define the navigation stack types
type RootStackParamList = {
  SmartAppliance: undefined;
};

type SmartApplianceScreenProps = NativeStackScreenProps<RootStackParamList, 'SmartAppliance'>;

const SmartApplianceScreen: React.FC<SmartApplianceScreenProps> = ({ navigation }) => {
  const [applianceId, setApplianceId] = useState('SmartFridge-001');
  const [syncAppliance, { loading }] = useMutation(SYNC_APPLIANCE_MUTATION, {
    refetchQueries: ['ItemsQuery'],
    onCompleted: (data) => {
      Alert.alert('Sync Complete', data.syncApplianceData);
      navigation.goBack();
    },
    onError: (error) => {
      Alert.alert('Sync Error', error.message);
    },
  });

  // Hardcoded householdId for now
  const HOUSEHOLD_ID = "1"; 

  const handleSync = () => {
    // Simulated data from a smart appliance scan
    const simulatedItems = [
      { name: 'Milk', quantity: 1, expirationDate: '2025-11-15' },
      { name: 'Eggs', quantity: 12, expirationDate: '2025-11-20' },
      { name: 'Yogurt', quantity: 4, expirationDate: '2025-12-01' },
      { name: 'New Item', quantity: 1, expirationDate: '2025-12-31' },
    ];

    const input = {
      applianceId,
      householdId: HOUSEHOLD_ID,
      items: simulatedItems,
    };

    syncAppliance({ variables: { input } });
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Smart Appliance Integration</Text>

      <View style={styles.connectionStatus}>
        <Text style={styles.statusText}>Connection Status: <Text style={styles.connected}>Connected</Text></Text>
        <Text style={styles.statusDetail}>Appliance Type: Smart Fridge (Samsung/LG API)</Text>
      </View>

      <Text style={styles.label}>Appliance ID</Text>
      <TextInput
        style={styles.input}
        value={applianceId}
        onChangeText={setApplianceId}
        placeholder="e.g., SmartFridge-001"
      />

      <Text style={styles.placeholderText}>
        In a real application, this screen would handle OAuth/API key exchange to connect to the smart appliance vendor's cloud service.
      </Text>

      <Button
        title={loading ? 'Syncing...' : 'Trigger Manual Sync'}
        onPress={handleSync}
        disabled={loading}
      />
      
      <View style={styles.placeholderSection}>
        <Text style={styles.sectionTitle}>Automatic Sync Settings</Text>
        <Text style={styles.placeholderText}>
          Toggle for real-time or scheduled syncs.
        </Text>
      </View>
    </ScrollView>
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
    marginBottom: 30,
    textAlign: 'center',
  },
  connectionStatus: {
    backgroundColor: '#E8F5E9',
    padding: 15,
    borderRadius: 10,
    marginBottom: 20,
    borderLeftWidth: 5,
    borderLeftColor: '#4CAF50',
  },
  statusText: {
    fontSize: 18,
    fontWeight: '600',
  },
  statusDetail: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  connected: {
    color: '#4CAF50',
    fontWeight: 'bold',
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    marginTop: 10,
    marginBottom: 5,
  },
  input: {
    backgroundColor: '#fff',
    padding: 10,
    borderRadius: 8,
    fontSize: 16,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  placeholderText: {
    fontSize: 14,
    color: '#999',
    fontStyle: 'italic',
    marginBottom: 30,
  },
  placeholderSection: {
    marginTop: 40,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
  }
});

export default SmartApplianceScreen;
