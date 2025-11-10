import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';

interface Item {
  id: string;
  name: string;
  quantity: number;
  expirationDate: string; // YYYY-MM-DD
  predictedExpirationDate: string | null;
  storageLocation: string;
}

interface ItemCardProps {
  item: Item;
  onPress: () => void;
}

const ItemCard: React.FC<ItemCardProps> = ({ item, onPress }) => {
  const isExpiringSoon = (dateString: string): boolean => {
    const today = new Date();
    const expirationDate = new Date(dateString);
    const diffTime = expirationDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays <= 7 && diffDays > 0;
  };

  const isExpired = (dateString: string): boolean => {
    const today = new Date();
    const expirationDate = new Date(dateString);
    return expirationDate.getTime() < today.getTime();
  };

  const getStatusStyle = (dateString: string) => {
    if (isExpired(dateString)) {
      return styles.expired;
    }
    if (isExpiringSoon(dateString)) {
      return styles.expiringSoon;
    }
    return styles.fresh;
  };

  const statusStyle = getStatusStyle(item.expirationDate);

  return (
    <TouchableOpacity style={[styles.card, statusStyle]} onPress={onPress}>
      <View style={styles.header}>
        <Text style={styles.name}>{item.name}</Text>
        <Text style={styles.quantity}>Qty: {item.quantity}</Text>
      </View>
      <View style={styles.details}>
        <Text style={styles.detailText}>Expires: {item.expirationDate}</Text>
      {item.predictedExpirationDate && (
        <Text style={styles.predictionText}>AI Predicts: {item.predictedExpirationDate}</Text>
      )}
        <Text style={styles.detailText}>Location: {item.storageLocation || 'N/A'}</Text>
      </View>
      <View style={[styles.statusIndicator, statusStyle]} />
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 12,
    marginBottom: 10,
    flexDirection: 'column',
    justifyContent: 'space-between',
    borderLeftWidth: 6,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 5,
  },
  name: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  quantity: {
    fontSize: 16,
    color: '#666',
  },
  details: {
    marginTop: 5,
  },
  detailText: {
    fontSize: 14,
    color: '#666',
  },
  statusIndicator: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    left: 0,
    width: 6,
    borderRadius: 3,
  },
  fresh: {
    borderLeftColor: '#4CAF50', // Green
  },
  expiringSoon: {
    borderLeftColor: '#FFC107', // Yellow/Amber
  },
  expired: {
    borderLeftColor: '#F44336', // Red
  },
  predictionText: {
    fontSize: 14,
    color: '#00796B', // Teal for AI prediction
    fontWeight: 'bold',
    marginTop: 2,
  }
});

export default ItemCard;
