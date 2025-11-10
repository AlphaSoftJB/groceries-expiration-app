import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { useQuery, useMutation } from '@apollo/client';
import {
  GET_PENDING_ALERTS,
  ACKNOWLEDGE_ALLERGEN_ALERT,
  DISMISS_ALERT,
  AllergenAlert,
  UserAction,
  getSeverityColor,
  getAllergenDisplayName,
} from '../graphql/nutritionQueries';

/**
 * Allergen Alerts Screen
 * Shows pending allergen warnings and allows users to acknowledge or dismiss them
 */
const AllergenAlertsScreen: React.FC = () => {
  const { data, loading, refetch } = useQuery(GET_PENDING_ALERTS);

  const [acknowledgeAlert] = useMutation(ACKNOWLEDGE_ALLERGEN_ALERT, {
    onCompleted: () => {
      refetch();
      Alert.alert('Alert Acknowledged', 'The allergen alert has been acknowledged.');
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  const [dismissAlert] = useMutation(DISMISS_ALERT, {
    onCompleted: () => {
      refetch();
      Alert.alert('Alert Dismissed', 'The allergen alert has been dismissed.');
    },
    onError: (error) => {
      Alert.alert('Error', error.message);
    },
  });

  const alerts: AllergenAlert[] = data?.getPendingAlerts || [];

  const handleAcknowledge = (alertId: string, action: UserAction) => {
    acknowledgeAlert({
      variables: {
        alertId,
        action,
      },
    });
  };

  const handleDismiss = (alertId: string, itemName: string) => {
    Alert.alert(
      'Dismiss Alert',
      \`Are you sure you want to dismiss the allergen alert for "\${itemName}"?\`,
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Dismiss',
          style: 'destructive',
          onPress: () => dismissAlert({ variables: { alertId } }),
        },
      ]
    );
  };

  const renderAlertItem = ({ item }: { item: AllergenAlert }) => {
    const severityColor = getSeverityColor(item.severity || 'MODERATE' as any);
    const displayName = getAllergenDisplayName(item.allergenType, item.allergenName);

    return (
      <View style={[styles.alertCard, { borderLeftColor: severityColor }]}>
        {/* Alert Header */}
        <View style={styles.alertHeader}>
          <View style={[styles.severityBadge, { backgroundColor: severityColor }]}>
            <Text style={styles.severityText}>
              {item.severity || 'MODERATE'}
            </Text>
          </View>
          <Text style={styles.itemName}>{item.item?.name || 'Unknown Item'}</Text>
        </View>

        {/* Alert Content */}
        <View style={styles.alertContent}>
          <Text style={styles.allergenLabel}>Contains Allergen:</Text>
          <Text style={styles.allergenName}>⚠️ {displayName}</Text>
          <Text style={styles.alertDate}>
            Detected: {new Date(item.createdAt).toLocaleDateString()}
          </Text>
        </View>

        {/* Action Buttons */}
        <View style={styles.actionButtons}>
          <TouchableOpacity
            style={[styles.actionButton, styles.proceedButton]}
            onPress={() => handleAcknowledge(item.id, UserAction.PROCEEDED)}
          >
            <Text style={styles.actionButtonText}>I'll Be Careful</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.actionButton, styles.removeButton]}
            onPress={() => handleAcknowledge(item.id, UserAction.REMOVED_ITEM)}
          >
            <Text style={styles.actionButtonText}>Remove Item</Text>
          </TouchableOpacity>
        </View>

        {/* Dismiss Button */}
        <TouchableOpacity
          style={styles.dismissButton}
          onPress={() => handleDismiss(item.id, item.item?.name || 'this item')}
        >
          <Text style={styles.dismissButtonText}>Dismiss Alert</Text>
        </TouchableOpacity>
      </View>
    );
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#F44336" />
        <Text style={styles.loadingText}>Loading allergen alerts...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>⚠️ Allergen Alerts</Text>
        <Text style={styles.subtitle}>
          {alerts.length} pending {alerts.length === 1 ? 'alert' : 'alerts'}
        </Text>
      </View>

      {/* Alerts List */}
      {alerts.length === 0 ? (
        <View style={styles.emptyState}>
          <Text style={styles.emptyIcon}>✅</Text>
          <Text style={styles.emptyText}>No Allergen Alerts</Text>
          <Text style={styles.emptySubtext}>
            All your items are safe based on your allergen profile
          </Text>
        </View>
      ) : (
        <FlatList
          data={alerts}
          renderItem={renderAlertItem}
          keyExtractor={(item) => item.id}
          contentContainerStyle={styles.listContainer}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: '#666',
  },
  header: {
    backgroundColor: '#fff',
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
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
  alertCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    borderLeftWidth: 6,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  alertHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  severityBadge: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
    marginRight: 12,
  },
  severityText: {
    color: '#fff',
    fontSize: 10,
    fontWeight: 'bold',
    textTransform: 'uppercase',
  },
  itemName: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    flex: 1,
  },
  alertContent: {
    marginBottom: 16,
  },
  allergenLabel: {
    fontSize: 12,
    color: '#999',
    marginBottom: 4,
  },
  allergenName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#F44336',
    marginBottom: 8,
  },
  alertDate: {
    fontSize: 12,
    color: '#666',
  },
  actionButtons: {
    flexDirection: 'row',
    marginBottom: 12,
    gap: 8,
  },
  actionButton: {
    flex: 1,
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  proceedButton: {
    backgroundColor: '#FFC107',
  },
  removeButton: {
    backgroundColor: '#F44336',
  },
  actionButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  dismissButton: {
    padding: 10,
    alignItems: 'center',
  },
  dismissButtonText: {
    color: '#999',
    fontSize: 14,
    textDecorationLine: 'underline',
  },
  emptyState: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
  },
  emptyIcon: {
    fontSize: 64,
    marginBottom: 16,
  },
  emptyText: {
    fontSize: 20,
    fontWeight: '600',
    color: '#666',
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#999',
    textAlign: 'center',
  },
});

export default AllergenAlertsScreen;
