import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Modal,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { useQuery, useMutation } from '@apollo/client';
import { Picker } from '@react-native-picker/picker';
import {
  GET_USER_ALLERGENS,
  ADD_USER_ALLERGEN,
  REMOVE_USER_ALLERGEN,
  UserAllergen,
  AllergenType,
  Severity,
  getAllergenDisplayName,
  getSeverityColor,
} from '../graphql/nutritionQueries';


/**
 * Allergen Management Screen
 * Allows users to manage their allergens and severity levels
 * Minimal UI Implementation
 */
const AllergenManagementScreen: React.FC = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedAllergen, setSelectedAllergen] = useState<AllergenType>(AllergenType.MILK);
  const [selectedSeverity, setSelectedSeverity] = useState<Severity>(Severity.MODERATE);
  const [customName, setCustomName] = useState('');

  // GraphQL queries and mutations
  const { data, loading, refetch } = useQuery(GET_USER_ALLERGENS);
  const [addAllergen, { loading: adding }] = useMutation(ADD_USER_ALLERGEN, {
    onCompleted: () => {
      setModalVisible(false);
      refetch();
      Alert.alert('Success', 'Allergen added successfully');
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });
  const [removeAllergen] = useMutation(REMOVE_USER_ALLERGEN, {
    onCompleted: () => {
      refetch();
      Alert.alert('Success', 'Allergen removed successfully');
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  const allergens: UserAllergen[] = data?.getUserAllergens || [];

  const handleAddAllergen = () => {
    addAllergen({
      variables: {
        input: {
          allergenType: selectedAllergen,
          severity: selectedSeverity,
          customAllergenName: selectedAllergen === AllergenType.CUSTOM ? customName : undefined,
        },
      },
    });
  };

  const handleRemoveAllergen = (id: string, name: string) => {
    Alert.alert(
      'Confirm Deletion',
      `Are you sure you want to remove ${name} from your allergen list?`,
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: () => removeAllergen({ variables: { id } }),
        },
      ]
    );
  };

  const renderAllergenItem = ({ item }: { item: UserAllergen }) => {
    const displayName = getAllergenDisplayName(item.allergenType, item.customAllergenName);
    const severityColor = getSeverityColor(item.severity);

    return (
      <View style={styles.allergenItem}>
        <View style={styles.allergenInfo}>
          <View style={[styles.severityIndicator, { backgroundColor: severityColor }]} />
          <View style={styles.allergenText}>
            <Text style={styles.allergenName}>{displayName}</Text>
            <Text style={styles.allergenSeverity}>
              Severity: {item.severity}
            </Text>
            {item.notes && (
              <Text style={styles.allergenNotes}>{item.notes}</Text>
            )}
          </View>
        </View>
        <TouchableOpacity
          style={styles.deleteButton}
          onPress={() => handleRemoveAllergen(item.id, displayName)}
        >
          <Text style={styles.deleteButtonText}>×</Text>
        </TouchableOpacity>
      </View>
    );
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#007AFF" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>My Allergens</Text>
        <Text style={styles.subtitle}>
          Manage your allergen profile to receive warnings about items
        </Text>
      </View>

      {/* Allergen List */}
      {allergens.length === 0 ? (
        <View style={styles.emptyState}>
          <Text style={styles.emptyText}>No allergens added</Text>
          <Text style={styles.emptySubtext}>
            Add allergens to receive warnings when scanning items
          </Text>
        </View>
      ) : (
        <FlatList
          data={allergens}
          renderItem={renderAllergenItem}
          keyExtractor={(item) => item.id}
          contentContainerStyle={styles.listContainer}
        />
      )}

      {/* Add Button */}
      <TouchableOpacity
        style={styles.addButton}
        onPress={() => setModalVisible(true)}
      >
        <Text style={styles.addButtonText}>+ Add Allergen</Text>
      </TouchableOpacity>

      {/* Add Allergen Modal */}
      <Modal
        visible={modalVisible}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Add New Allergen</Text>

            {/* Allergen Type Picker */}
            <Text style={styles.label}>Allergen Type</Text>
            <View style={styles.pickerContainer}>
              <Picker
                selectedValue={selectedAllergen}
                onValueChange={(value) => setSelectedAllergen(value)}
                style={styles.picker}
              >
                <Picker.Item label="Milk/Dairy" value={AllergenType.MILK} />
                <Picker.Item label="Eggs" value={AllergenType.EGGS} />
                <Picker.Item label="Fish" value={AllergenType.FISH} />
                <Picker.Item label="Shellfish" value={AllergenType.SHELLFISH} />
                <Picker.Item label="Tree Nuts" value={AllergenType.TREE_NUTS} />
                <Picker.Item label="Peanuts" value={AllergenType.PEANUTS} />
                <Picker.Item label="Wheat" value={AllergenType.WHEAT} />
                <Picker.Item label="Soy" value={AllergenType.SOY} />
                <Picker.Item label="Sesame" value={AllergenType.SESAME} />
                <Picker.Item label="Gluten" value={AllergenType.GLUTEN} />
              </Picker>
            </View>

            {/* Severity Picker */}
            <Text style={styles.label}>Severity Level</Text>
            <View style={styles.pickerContainer}>
              <Picker
                selectedValue={selectedSeverity}
                onValueChange={(value) => setSelectedSeverity(value)}
                style={styles.picker}
              >
                <Picker.Item label="🟢 Mild" value={Severity.MILD} />
                <Picker.Item label="🟡 Moderate" value={Severity.MODERATE} />
                <Picker.Item label="🟠 Severe" value={Severity.SEVERE} />
                <Picker.Item label="🔴 Life Threatening" value={Severity.LIFE_THREATENING} />
              </Picker>
            </View>

            {/* Buttons */}
            <View style={styles.modalButtons}>
              <TouchableOpacity
                style={[styles.modalButton, styles.cancelButton]}
                onPress={() => setModalVisible(false)}
              >
                <Text style={styles.cancelButtonText}>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.modalButton, styles.saveButton]}
                onPress={handleAddAllergen}
                disabled={adding}
              >
                {adding ? (
                  <ActivityIndicator color="#FFF" />
                ) : (
                  <Text style={styles.saveButtonText}>Add</Text>
                )}
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  header: {
    backgroundColor: '#FFF',
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#E0E0E0',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 14,
    color: '#666',
  },
  listContainer: {
    padding: 16,
  },
  allergenItem: {
    flexDirection: 'row',
    backgroundColor: '#FFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  allergenInfo: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
  },
  severityIndicator: {
    width: 8,
    height: 40,
    borderRadius: 4,
    marginRight: 12,
  },
  allergenText: {
    flex: 1,
  },
  allergenName: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  allergenSeverity: {
    fontSize: 14,
    color: '#666',
    marginBottom: 2,
  },
  allergenNotes: {
    fontSize: 12,
    color: '#999',
    fontStyle: 'italic',
  },
  deleteButton: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: '#F44336',
    justifyContent: 'center',
    alignItems: 'center',
  },
  deleteButtonText: {
    color: '#FFF',
    fontSize: 24,
    fontWeight: 'bold',
  },
  emptyState: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
  },
  emptyText: {
    fontSize: 18,
    fontWeight: '600',
    color: '#666',
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#999',
    textAlign: 'center',
  },
  addButton: {
    backgroundColor: '#007AFF',
    margin: 16,
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
  },
  addButtonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContent: {
    backgroundColor: '#FFF',
    borderRadius: 16,
    padding: 24,
    width: '85%',
    maxWidth: 400,
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 20,
    textAlign: 'center',
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
    marginTop: 12,
  },
  pickerContainer: {
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    overflow: 'hidden',
  },
  picker: {
    height: 50,
  },
  modalButtons: {
    flexDirection: 'row',
    marginTop: 24,
    gap: 12,
  },
  modalButton: {
    flex: 1,
    padding: 14,
    borderRadius: 8,
    alignItems: 'center',
  },
  cancelButton: {
    backgroundColor: '#F5F5F5',
  },
  cancelButtonText: {
    color: '#666',
    fontSize: 16,
    fontWeight: '600',
  },
  saveButton: {
    backgroundColor: '#007AFF',
  },
  saveButtonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default AllergenManagementScreen;
