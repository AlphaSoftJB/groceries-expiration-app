import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { useQuery, gql } from '@apollo/client';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

// Define the GraphQL Query (same as in HomeScreen)
const ITEMS_QUERY = gql\`
  query ItemsQuery($householdId: ID!) {
    itemsByHousehold(householdId: $householdId) {
      id
      name
      expirationDate
      predictedExpirationDate
      storageLocation
    }
  }
\`;

// Define the navigation stack types
type RootStackParamList = {
  ARView: undefined;
  ItemDetail: { itemId: string };
};

type ARViewScreenProps = NativeStackScreenProps<RootStackParamList, 'ARView'>;

const ARViewScreen: React.FC<ARViewScreenProps> = ({ navigation }) => {
  // Hardcoded householdId for now
  const HOUSEHOLD_ID = "1"; 

  const { loading, error, data } = useQuery(ITEMS_QUERY, {
    variables: { householdId: HOUSEHOLD_ID },
    fetchPolicy: 'cache-and-network',
  });

  const getStatusColor = (dateString: string): string => {
    const today = new Date();
    const expirationDate = new Date(dateString);
    const diffTime = expirationDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays <= 0) return '#F44336'; // Red (Expired)
    if (diffDays <= 7) return '#FFC107'; // Yellow/Amber (Expiring Soon)
    return '#4CAF50'; // Green (Fresh)
  };

  const renderAROverlay = (item: any) => {
    const statusColor = getStatusColor(item.expirationDate);
    const predictedColor = item.predictedExpirationDate ? getStatusColor(item.predictedExpirationDate) : '#9E9E9E';

    return (
      <TouchableOpacity 
        key={item.id} 
        style={[styles.arTag, { borderColor: statusColor }]}
        onPress={() => navigation.navigate('ItemDetail', { itemId: item.id })}
      >
        <Text style={styles.arTagName}>{item.name}</Text>
        <Text style={[styles.arTagText, { color: statusColor }]}>Expires: {item.expirationDate}</Text>
        {item.predictedExpirationDate && (
          <Text style={[styles.arTagText, { color: predictedColor, fontWeight: 'bold' }]}>AI: {item.predictedExpirationDate}</Text>
        )}
        <Text style={styles.arTagLocation}>Location: {item.storageLocation || 'Unknown'}</Text>
      </TouchableOpacity>
    );
  };

  return (
    <View style={styles.container}>
      <View style={styles.cameraView}>
        <Text style={styles.cameraText}>[ Simulated Camera View of Fridge Interior ]</Text>
        <Text style={styles.cameraText}>Point your phone at the fridge to see AR tags.</Text>
      </View>

      <ScrollView style={styles.overlayContainer}>
        <Text style={styles.overlayTitle}>Simulated AR Overlays</Text>
        {loading && <Text style={styles.loadingText}>Loading item data...</Text>}
        {error && <Text style={styles.errorText}>Error: {error.message}</Text>}
        {data?.itemsByHousehold.map(renderAROverlay)}
        {data?.itemsByHousehold.length === 0 && !loading && (
          <Text style={styles.loadingText}>No items to display in AR.</Text>
        )}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000', // Black background to simulate camera view
  },
  cameraView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#111',
    borderBottomWidth: 2,
    borderBottomColor: '#333',
  },
  cameraText: {
    color: '#fff',
    fontSize: 18,
    marginVertical: 5,
  },
  overlayContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    padding: 20,
  },
  overlayTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 15,
    textAlign: 'center',
    backgroundColor: 'rgba(0,0,0,0.5)',
    padding: 5,
    borderRadius: 5,
  },
  arTag: {
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    padding: 10,
    borderRadius: 8,
    marginBottom: 10,
    borderWidth: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.8,
    shadowRadius: 4,
    elevation: 5,
  },
  arTagName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  arTagText: {
    fontSize: 14,
    marginTop: 2,
  },
  arTagLocation: {
    fontSize: 12,
    color: '#666',
    marginTop: 5,
  },
  loadingText: {
    color: '#fff',
    textAlign: 'center',
    marginTop: 20,
  },
  errorText: {
    color: 'red',
    textAlign: 'center',
    marginTop: 20,
  }
});

export default ARViewScreen;
