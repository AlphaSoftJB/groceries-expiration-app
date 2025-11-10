import { ApolloClient, InMemoryCache, HttpLink, from } from '@apollo/client';

// Configure Apollo Client to connect to Java GraphQL backend
const httpLink = new HttpLink({
  uri: import.meta.env.VITE_GRAPHQL_URL || 'http://localhost:8080/graphql',
  credentials: 'include',
});

export const apolloClient = new ApolloClient({
  link: from([httpLink]),
  cache: new InMemoryCache(),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'cache-and-network',
    },
    query: {
      fetchPolicy: 'network-only',
    },
  },
});
