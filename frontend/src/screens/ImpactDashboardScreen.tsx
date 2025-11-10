import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { useQuery, gql } from '@apollo/client';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

// Define the GraphQL Query
const SUSTAINABILITY_QUERY = gql\`
  query SustainabilityQuery {
    sustainabilityMetrics {
      totalCo2SavedKg
    }
  }
\`;

// Define the navigation stack types
type RootStackParamList = {
  ImpactDashboard: undefined;
};

type ImpactDashboardScreenProps = NativeStackScreenProps<RootStackParamList, 'ImpactDashboard'>;

const ImpactDashboardScreen: React.FC<ImpactDashboardScreenProps> = () => {
  const { loading, error, data } = useQuery(SUSTAINABILITY_QUERY);

  if (loading) {
    return <Text style={styles.loadingText}>Loading impact data...</Text>;
  }

  if (error) {
    return <Text style={styles.errorText}>Error loading impact data: {error.message}</Text>;
  }

  const totalCo2SavedKg = data?.sustainabilityMetrics?.totalCo2SavedKg || 0;
  const totalCo2SavedTons = (totalCo2SavedKg / 1000).toFixed(3);

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Your Impact Dashboard</Text>

      <View style={styles.metricCard}>
        <Text style={styles.metricValue}>{totalCo2SavedKg.toFixed(2)} kg</Text>
        <Text style={styles.metricLabel}>Total CO₂ Saved (Food Waste Prevention)</Text>
        <Text style={styles.metricSubLabel}>~ {totalCo2SavedTons} tons</Text>
      </View>

      <View style={styles.gamificationSection}>
        <Text style={styles.sectionTitle}>Gamification & Rewards</Text>
        <Text style={styles.placeholderText}>
          This section will display your achievements, badges, and NFT rewards (e.g., "Waste Warrior", "Zero-Waste Hero").
        </Text>
        <View style={styles.achievementPlaceholder}>
          <Text style={styles.achievementText}>Achievement: First 10kg Saved (NFT Minted)</Text>
          <Text style={styles.achievementText}>Reward: 50 $FOOD Tokens</Text>
        </View>
      </View>

      <View style={styles.placeholderSection}>
        <Text style={styles.sectionTitle}>Sustainability Partners</Text>
        <Text style={styles.placeholderText}>
          Display partner integrations for food donation and composting services based on location.
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
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 30,
    textAlign: 'center',
    color: '#1B5E20',
  },
  metricCard: {
    backgroundColor: '#E8F5E9', // Light Green
    padding: 25,
    borderRadius: 15,
    alignItems: 'center',
    marginBottom: 30,
    borderWidth: 2,
    borderColor: '#4CAF50',
  },
  metricValue: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#1B5E20',
  },
  metricLabel: {
    fontSize: 18,
    textAlign: 'center',
    marginTop: 5,
    color: '#388E3C',
  },
  metricSubLabel: {
    fontSize: 14,
    color: '#66BB6A',
    marginTop: 5,
  },
  gamificationSection: {
    marginBottom: 30,
  },
  placeholderSection: {
    marginBottom: 30,
  },
  sectionTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
    paddingBottom: 5,
  },
  placeholderText: {
    fontSize: 16,
    color: '#666',
    fontStyle: 'italic',
    marginBottom: 10,
  },
  achievementPlaceholder: {
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 10,
    borderLeftWidth: 5,
    borderLeftColor: '#00BCD4', // Cyan for blockchain/NFT
  },
  achievementText: {
    fontSize: 16,
    color: '#006064',
    fontWeight: '600',
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
  }
});

export default ImpactDashboardScreen;
