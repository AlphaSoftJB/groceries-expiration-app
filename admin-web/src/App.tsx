import { ApolloProvider } from '@apollo/client';
import { apolloClient } from './apolloClient';
import AdminDashboard from './AdminDashboard';
import './index.css';

function App() {
  return (
    <ApolloProvider client={apolloClient}>
      <AdminDashboard />
    </ApolloProvider>
  );
}

export default App;
