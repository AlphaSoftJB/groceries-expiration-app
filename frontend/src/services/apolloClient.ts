import { ApolloClient, InMemoryCache, createHttpLink } from '@apollo/client';
import { Platform } from 'react-native';

/**
 * GraphQL Endpoint Configuration
 * 
 * For React Native:
 * - iOS Simulator: Use 'localhost'
 * - Android Emulator: Use '10.0.2.2' (emulator's host loopback)
 * - Physical Device: Use your machine's local IP (e.g., '192.168.1.100')
 * 
 * To find your machine's IP:
 * - macOS/Linux: Run `ifconfig | grep "inet " | grep -v 127.0.0.1`
 * - Windows: Run `ipconfig` and look for IPv4 Address
 */

// Change this to your machine's IP address when testing on physical devices
const HOST_IP = 'localhost'; // e.g., '192.168.1.100'

const getGraphQLEndpoint = () => {
  if (Platform.OS === 'android') {
    // Android emulator uses 10.0.2.2 to access host machine
    return `http://10.0.2.2:8080/graphql`;
  }
  // iOS simulator and web can use localhost
  return `http://${HOST_IP}:8080/graphql`;
};

const GRAPHQL_ENDPOINT = getGraphQLEndpoint();

console.log('[Apollo Client] Connecting to:', GRAPHQL_ENDPOINT); 

const httpLink = createHttpLink({
  uri: GRAPHQL_ENDPOINT,
});

const client = new ApolloClient({
  link: httpLink,
  cache: new InMemoryCache({
    typePolicies: {
      Query: {
        fields: {
          // Merge strategies for list queries
          getUserAllergens: {
            merge(existing, incoming) {
              return incoming;
            },
          },
          getPendingAlerts: {
            merge(existing, incoming) {
              return incoming;
            },
          },
        },
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'cache-and-network',
      errorPolicy: 'all',
    },
    query: {
      fetchPolicy: 'network-only',
      errorPolicy: 'all',
    },
    mutate: {
      errorPolicy: 'all',
    },
  },
});

export default client;
