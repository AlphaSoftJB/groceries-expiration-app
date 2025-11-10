import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Button, Alert, TextInput, ScrollView } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useQuery, useMutation, gql } from '@apollo/client';
import { NutritionInfoCard } from '../components/NutritionInfoCard';

// --- GraphQL Operations ---

const ITEM_DETAIL_QUERY = gql\`
  query ItemDetailQuery($itemId: ID!) {
    item(id: $itemId) {
      id
      name
      quantity
      expirationDate
      predictedExpirationDate
      storageLocation
      household {
        id
        name
      }
      addedBy {
        name
      }
    }
  }
\`;

const UPDATE_ITEM_MUTATION = gql\`
  mutation UpdateItem($input: UpdateItemInput!) {
    updateItem(input: $input) {
      id
      name
      expirationDate
      quantity
      storageLocation
    }
  }
\`;

const DELETE_ITEM_MUTATION = gql\`
  mutation DeleteItem($itemId: ID!) {
    deleteItem(itemId: $itemId)
  }
\`;

const MARK_AS_USED_MUTATION = gql\`
  mutation MarkItemAsUsed($itemId: ID!) {
    markItemAsUsed(itemId: $itemId) {
      id
      name
    }
  }
\`;

// --- Component Logic ---

type RootStackParamList = {
  Home: undefined;
  ItemDetail: { itemId: string };
};

type ItemDetailScreenProps = NativeStackScreenProps<RootStackParamList, 'ItemDetail'>;

const ItemDetailScreen: React.FC<ItemDetailScreenProps> = ({ route, navigation }) => {
  const { itemId } = route.params;

  const { loading: queryLoading, error: queryError, data, refetch } = useQuery(ITEM_DETAIL_QUERY, {
    variables: { itemId },
  });

  const [name, setName] = useState('');
  const [quantity, setQuantity] = useState('');
  const [expirationDate, setExpirationDate] = useState('');
  const [storageLocation, setStorageLocation] = useState('');
  const [predictedExpirationDate, setPredictedExpirationDate] = useState<string | null>(null);

  useEffect(() => {
    if (data?.item) {
      setName(data.item.name);
      setQuantity(String(data.item.quantity));
      setExpirationDate(data.item.expirationDate);
      setStorageLocation(data.item.storageLocation || '');
      setPredictedExpirationDate(data.item.predictedExpirationDate);
    }
  }, [data]);

  const [updateItem, { loading: updateLoading }] = useMutation(UPDATE_ITEM_MUTATION, {
    onCompleted: () => {
      Alert.alert('Success', 'Item updated successfully!');
      refetch();
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  const [deleteItem, { loading: deleteLoading }] = useMutation(DELETE_ITEM_MUTATION, {
    refetchQueries: ['ItemsQuery'], // To update the home screen list
    onCompleted: () => {
      Alert.alert('Success', 'Item deleted successfully!');
      navigation.goBack();
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  const [markAsUsed, { loading: markAsUsedLoading }] = useMutation(MARK_AS_USED_MUTATION, {
    refetchQueries: ['ItemsQuery', 'SustainabilityQuery'], // Update both lists
    onCompleted: (data) => {
      Alert.alert('Success', \`\${data.markItemAsUsed.name} marked as used! Your sustainability score has been updated.\`);
      navigation.goBack();
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  const handleUpdate = () => {
    if (!name || !quantity || !expirationDate) {
      Alert.alert('Error', 'Please fill in all required fields.');
      return;
    }

    const input = {
      itemId: itemId,
      name: name,
      quantity: parseInt(quantity, 10),
      expirationDate: expirationDate,
      storageLocation: storageLocation || null,
    };

    updateItem({ variables: { input } });
  };

  const handleDelete = () => {
    Alert.alert(
      'Confirm Deletion',
      'Are you sure you want to delete this item? This action cannot be undone.',
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: () => deleteItem({ variables: { itemId } }),
        },
      ],
    );
  };

  if (queryLoading) {
    return <Text style={styles.loadingText}>Loading item details...</Text>;
  }

  if (queryError) {
    return <Text style={styles.errorText}>Error loading item: {queryError.message}</Text>;
  }

  if (!data?.item) {
    return <Text style={styles.errorText}>Item not found.</Text>;
  }

  const item = data.item;

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Edit Item: {item.name}</Text>
      
      <View style={styles.infoBox}>
        <Text style={styles.infoText}>Household: {item.household.name}</Text>
        <Text style={styles.infoText}>Added By: {item.addedBy.name}</Text>
      </View>

      {predictedExpirationDate && (
        <View style={styles.predictionBox}>
          <Text style={styles.predictionText}>AI Predicted Expiration: {predictedExpirationDate}</Text>
        </View>
      )}

      <Text style={styles.label}>Item Name*</Text>
      <TextInput style={styles.input} value={name} onChangeText={setName} />

      <Text style={styles.label}>Quantity*</Text>
      <TextInput style={styles.input} value={quantity} onChangeText={setQuantity} keyboardType="numeric" />

      <Text style={styles.label}>Expiration Date (YYYY-MM-DD)*</Text>
      <TextInput style={styles.input} value={expirationDate} onChangeText={setExpirationDate} placeholder="YYYY-MM-DD" />

      <Text style={styles.label}>Storage Location (Optional)</Text>
      <TextInput style={styles.input} value={storageLocation} onChangeText={setStorageLocation} placeholder="e.g., Fridge - Top Shelf" />

      <Button
        title={updateLoading ? 'Updating...' : 'Update Item'}
        onPress={handleUpdate}
        disabled={updateLoading || markAsUsedLoading}
      />

      <View style={styles.markAsUsedButtonContainer}>
        <Button
          title={markAsUsedLoading ? 'Marking...' : 'Mark as Used'}
          onPress={() => markAsUsed({ variables: { itemId } })}
          color="#4CAF50"
          disabled={markAsUsedLoading || updateLoading}
        />
      </View>

      <View style={styles.deleteButtonContainer}>
        <Button
          title={deleteLoading ? 'Deleting...' : 'Delete Item'}
          onPress={handleDelete}
          color="red"
          disabled={deleteLoading}
        />
      </View>

      {/* Nutrition Information Section */}
      <View style={styles.nutritionSection}>
        <Text style={styles.sectionTitle}>Nutrition Information</Text>
        <NutritionInfoCard itemId={itemId} />
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
    marginBottom: 20,
    textAlign: 'center',
  },
  infoBox: {
    backgroundColor: '#e0f7fa',
    padding: 10,
    borderRadius: 8,
    marginBottom: 20,
  },
  predictionBox: {
    backgroundColor: '#e8f5e9', // Light green
    padding: 10,
    borderRadius: 8,
    marginBottom: 20,
    borderLeftWidth: 4,
    borderLeftColor: '#4CAF50',
  },
  predictionText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#1B5E20',
  },
  infoText: {
    fontSize: 14,
    color: '#006064',
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
    marginBottom: 15,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  markAsUsedButtonContainer: {
    marginTop: 20,
  },
  deleteButtonContainer: {
    marginTop: 20,
  },
  loadingText: {
    textAlign: 'center',
    marginTop: 50,
    fontSize: 18,
  },
  errorText: {
    textAlign: 'center',
    marginTop: 50,
    fontSize: 18,
    color: 'red',
  },
  nutritionSection: {
    marginTop: 30,
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333',
  },
});

export default ItemDetailScreen;
