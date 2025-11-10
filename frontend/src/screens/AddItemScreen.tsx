import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, Button, Alert, ScrollView, TouchableOpacity } from 'react-native';
import { useMutation, gql } from '@apollo/client';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

// Define the GraphQL Mutation
const PROCESS_OCR_MUTATION = gql\`
  mutation ProcessOCR($imageBase64: String!) {
    processImageForOCR(imageBase64: $imageBase64) {
      name
      quantity
      expirationDate
    }
  }
\`;

const CREATE_ITEM_MUTATION = gql\`
  mutation CreateItem($input: CreateItemInput!) {
    createItem(input: $input) {
      id
      name
      expirationDate
      quantity
    }
  }
\`;

// Define the navigation stack types
type RootStackParamList = {
  Home: undefined;
  AddItem: undefined;
};

type AddItemScreenProps = NativeStackScreenProps<RootStackParamList, 'AddItem'>;

const AddItemScreen: React.FC<AddItemScreenProps> = ({ navigation, route }) => {
  const prefillData = route?.params?.prefillData || {};
  const [name, setName] = useState(prefillData.name || '');
  const [quantity, setQuantity] = useState('1');
  const [expirationDate, setExpirationDate] = useState(''); // Format YYYY-MM-DD
  const [storageLocation, setStorageLocation] = useState(prefillData.storageLocation || '');
  const [ocrData, setOcrData] = useState<{ name: string, quantity: number, expirationDate: string | null } | null>(null);
  
  // Hardcoded householdId for now
  const HOUSEHOLD_ID = "1"; 

  const [createItem, { loading: createLoading }] = useMutation(CREATE_ITEM_MUTATION, {
    // Refetch the items query on success to update the HomeScreen list
    refetchQueries: ['ItemsQuery'],
    onCompleted: (data) => {
      Alert.alert('Success', \`Item \${data.createItem.name} added!\`);
      navigation.goBack();
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  const [processOCR, { loading: ocrLoading }] = useMutation(PROCESS_OCR_MUTATION, {
    onCompleted: (data) => {
      const result = data.processImageForOCR;
      setOcrData(result);
      setName(result.name);
      setQuantity(String(result.quantity));
      setExpirationDate(result.expirationDate || '');
      Alert.alert('OCR Success', \`Found \${result.name} with expiration \${result.expirationDate || 'N/A'}\`);
    },
    onError: (error) => {
      Alert.alert('OCR Error', error.message);
    },
  });

  const handleOCR = () => {
    // In a real app, this would open the camera/gallery and get the image data.
    // For now, we simulate the image data based on a simple prompt.
    Alert.prompt(
      'Simulate OCR Input',
      'Enter a keyword to simulate image content (e.g., "milk" or "bread"):',
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'Process',
          onPress: (text) => {
            if (text) {
              // Base64 encoding of the keyword for the placeholder service
              const base64SimulatedImage = Buffer.from(text).toString('base64');
              processOCR({ variables: { imageBase64: base64SimulatedImage } });
            }
          },
        },
      ],
      'plain-text',
      'milk'
    );
  };

  const handleSubmit = () => {
    if (!name || !quantity || !expirationDate) {
      Alert.alert('Error', 'Please fill in all required fields (Name, Quantity, Expiration Date).');
      return;
    }

    const input = {
      name,
      quantity: parseInt(quantity, 10),
      expirationDate,
      storageLocation: storageLocation || null,
      householdId: HOUSEHOLD_ID,
    };

    createItem({ variables: { input } });
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Add New Item to Household {HOUSEHOLD_ID}</Text>

      <View style={styles.buttonRow}>
        <TouchableOpacity 
          style={styles.cameraButton} 
          onPress={() => navigation.navigate('Camera')}
        >
          <Text style={styles.cameraButtonText}>📷 Camera</Text>
        </TouchableOpacity>
        <TouchableOpacity 
          style={styles.barcodeButton} 
          onPress={() => navigation.navigate('BarcodeScanner')}
        >
          <Text style={styles.barcodeButtonText}>🔍 Barcode</Text>
        </TouchableOpacity>
      </View>

      {ocrData && (
        <Text style={styles.ocrStatus}>
          OCR Data Loaded. Review and Edit Details Below.
        </Text>
      )}

      <Text style={styles.label}>Item Name*</Text>
      <TextInput
        style={styles.input}
        value={name}
        onChangeText={setName}
        placeholder="e.g., Milk, Eggs, Yogurt"
      />

      <Text style={styles.label}>Quantity*</Text>
      <TextInput
        style={styles.input}
        value={quantity}
        onChangeText={setQuantity}
        keyboardType="numeric"
        placeholder="e.g., 1, 6, 12"
      />

      <Text style={styles.label}>Expiration Date (YYYY-MM-DD)*</Text>
      <TextInput
        style={styles.input}
        value={expirationDate}
        onChangeText={setExpirationDate}
        placeholder="e.g., 2025-12-31"
      />

      <Text style={styles.label}>Storage Location (Optional)</Text>
      <TextInput
        style={styles.input}
        value={storageLocation}
        onChangeText={setStorageLocation}
        placeholder="e.g., Fridge - Top Shelf, Pantry"
      />

      <Button
        title={createLoading ? 'Adding...' : 'Add Item'}
        onPress={handleSubmit}
        disabled={createLoading}
      />
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
  buttonRow: {
    flexDirection: 'row',
    gap: 10,
    marginBottom: 20,
  },
  cameraButton: {
    flex: 1,
    backgroundColor: '#2196F3',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  cameraButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  barcodeButton: {
    flex: 1,
    backgroundColor: '#4CAF50',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  barcodeButtonText: {
    color: '#fff',
    fontSize: 16,
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
});

export default AddItemScreen;
