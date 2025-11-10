import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, TextInput, Button, Alert } from 'react-native';
import { useQuery, useMutation, gql } from '@apollo/client';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

// --- GraphQL Definitions ---

const SHOPPING_LIST_QUERY = gql\`
  query ShoppingListQuery($householdId: ID!) {
    shoppingListByHousehold(householdId: $householdId) {
      id
      items {
        id
        name
        quantity
        isPurchased
      }
    }
    smartShoppingSuggestions(householdId: $householdId)
  }
\`;

const ADD_ITEM_MUTATION = gql\`
  mutation AddItemToShoppingList($input: AddItemToShoppingListInput!) {
    addItemToShoppingList(input: $input) {
      id
      name
    }
  }
\`;

const TOGGLE_ITEM_MUTATION = gql\`
  mutation ToggleShoppingListItem($itemId: ID!) {
    toggleShoppingListItem(itemId: $itemId) {
      id
      isPurchased
    }
  }
\`;

// --- Component Definitions ---

type RootStackParamList = {
  ShoppingList: undefined;
};

type ShoppingListScreenProps = NativeStackScreenProps<RootStackParamList, 'ShoppingList'>;

interface ShoppingListItem {
  id: string;
  name: string;
  quantity: number;
  isPurchased: boolean;
}

const ShoppingListScreen: React.FC<ShoppingListScreenProps> = () => {
  const HOUSEHOLD_ID = "1"; 
  const [newItemName, setNewItemName] = useState('');
  const [newItemQuantity, setNewItemQuantity] = useState('1');

  const { loading, error, data, refetch } = useQuery(SHOPPING_LIST_QUERY, {
    variables: { householdId: HOUSEHOLD_ID },
    fetchPolicy: 'cache-and-network',
  });

  const [addItem] = useMutation(ADD_ITEM_MUTATION, {
    refetchQueries: ['ShoppingListQuery'],
    onCompleted: () => {
      setNewItemName('');
      setNewItemQuantity('1');
    },
    onError: (err) => Alert.alert('Error', err.message),
  });

  const [toggleItem] = useMutation(TOGGLE_ITEM_MUTATION, {
    refetchQueries: ['ShoppingListQuery'],
    onError: (err) => Alert.alert('Error', err.message),
  });

  const handleAddItem = () => {
    if (!newItemName.trim()) return;
    const listId = data?.shoppingListByHousehold?.id;
    if (!listId) return Alert.alert('Error', 'Shopping list not found.');

    addItem({
      variables: {
        input: {
          listId,
          name: newItemName.trim(),
          quantity: parseInt(newItemQuantity, 10) || 1,
        },
      },
    });
  };

  const handleToggleItem = (itemId: string) => {
    toggleItem({ variables: { itemId } });
  };

  const handleAddSuggestion = (suggestion: string) => {
    const listId = data?.shoppingListByHousehold?.id;
    if (!listId) return Alert.alert('Error', 'Shopping list not found.');

    addItem({
      variables: {
        input: {
          listId,
          name: suggestion,
          quantity: 1,
        },
      },
    });
  };

  if (loading) return <Text style={styles.loadingText}>Loading shopping list...</Text>;
  if (error) return <Text style={styles.errorText}>Error: {error.message}</Text>;

  const shoppingList = data?.shoppingListByHousehold;
  const suggestions = data?.smartShoppingSuggestions || [];
  const items: ShoppingListItem[] = shoppingList?.items || [];

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Shopping List</Text>

      {/* Smart Suggestions */}
      <View style={styles.suggestionsContainer}>
        <Text style={styles.sectionTitle}>Smart Suggestions</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.suggestionsScroll}>
          {suggestions.map((suggestion, index) => (
            <TouchableOpacity 
              key={index} 
              style={styles.suggestionPill}
              onPress={() => handleAddSuggestion(suggestion)}
            >
              <Text style={styles.suggestionText}>+ {suggestion}</Text>
            </TouchableOpacity>
          ))}
        </ScrollView>
        <Text style={styles.placeholderText}>
          Suggestions based on low inventory, consumption history, and expiring items.
        </Text>
      </View>

      {/* Add New Item Form */}
      <View style={styles.addItemContainer}>
        <Text style={styles.sectionTitle}>Add Item</Text>
        <View style={styles.inputRow}>
          <TextInput
            style={[styles.input, { flex: 3 }]}
            placeholder="Item Name (e.g., Milk)"
            value={newItemName}
            onChangeText={setNewItemName}
          />
          <TextInput
            style={[styles.input, { flex: 1, marginLeft: 10 }]}
            placeholder="Qty"
            keyboardType="numeric"
            value={newItemQuantity}
            onChangeText={setNewItemQuantity}
          />
        </View>
        <Button title="Add to List" onPress={handleAddItem} disabled={!newItemName.trim()} />
      </View>

      {/* Shopping List */}
      <View style={styles.listContainer}>
        <Text style={styles.sectionTitle}>Items to Buy</Text>
        {items.filter(item => !item.isPurchased).map(item => (
          <TouchableOpacity 
            key={item.id} 
            style={styles.listItem}
            onPress={() => handleToggleItem(item.id)}
          >
            <Text style={styles.listItemText}>{item.name} (x{item.quantity})</Text>
            <View style={styles.checkbox} />
          </TouchableOpacity>
        ))}
      </View>

      {/* Purchased Items */}
      <View style={styles.listContainer}>
        <Text style={styles.sectionTitle}>Purchased</Text>
        {items.filter(item => item.isPurchased).map(item => (
          <TouchableOpacity 
            key={item.id} 
            style={styles.listItemPurchased}
            onPress={() => handleToggleItem(item.id)}
          >
            <Text style={styles.listItemTextPurchased}>{item.name} (x{item.quantity})</Text>
            <Text style={styles.checkmark}>✓</Text>
          </TouchableOpacity>
        ))}
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
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
    color: '#333',
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#4CAF50',
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
  // Suggestions
  suggestionsContainer: {
    marginBottom: 20,
    paddingBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  suggestionsScroll: {
    flexDirection: 'row',
    marginBottom: 10,
  },
  suggestionPill: {
    backgroundColor: '#E0F7FA', // Light Cyan
    paddingVertical: 8,
    paddingHorizontal: 15,
    borderRadius: 20,
    marginRight: 10,
    borderWidth: 1,
    borderColor: '#00BCD4',
  },
  suggestionText: {
    color: '#006064',
    fontWeight: '600',
  },
  placeholderText: {
    fontSize: 14,
    color: '#999',
    fontStyle: 'italic',
  },
  // Add Item
  addItemContainer: {
    marginBottom: 30,
    padding: 15,
    backgroundColor: '#fff',
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  inputRow: {
    flexDirection: 'row',
    marginBottom: 10,
  },
  input: {
    backgroundColor: '#f9f9f9',
    padding: 10,
    borderRadius: 8,
    fontSize: 16,
    borderWidth: 1,
    borderColor: '#eee',
  },
  // List
  listContainer: {
    marginBottom: 30,
  },
  listItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 8,
    marginBottom: 8,
    borderLeftWidth: 5,
    borderLeftColor: '#FFC107', // Yellow for items to buy
  },
  listItemPurchased: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#E8F5E9', // Light Green
    padding: 15,
    borderRadius: 8,
    marginBottom: 8,
    borderLeftWidth: 5,
    borderLeftColor: '#4CAF50',
    opacity: 0.7,
  },
  listItemText: {
    fontSize: 16,
    color: '#333',
  },
  listItemTextPurchased: {
    fontSize: 16,
    color: '#388E3C',
    textDecorationLine: 'line-through',
  },
  checkbox: {
    width: 20,
    height: 20,
    borderRadius: 4,
    borderWidth: 2,
    borderColor: '#FFC107',
  },
  checkmark: {
    fontSize: 18,
    color: '#4CAF50',
    fontWeight: 'bold',
  }
});

export default ShoppingListScreen;
