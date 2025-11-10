# Groceries Expiration Tracking App - Frontend

This is the mobile application built with React Native and TypeScript, serving as the user interface for the Groceries Expiration Tracking App. It communicates with the Kotlin/Spring Boot backend via GraphQL.

## 🛠️ Tech Stack

*   **Framework**: React Native
*   **Language**: TypeScript
*   **State/API Management**: Apollo Client (for GraphQL)
*   **Navigation**: React Navigation

## 🚀 Getting Started

### 1. Prerequisites

*   Node.js (v18+)
*   npm or yarn
*   React Native development environment set up (Android Studio/Xcode)
*   The **Backend** must be running on \`http://localhost:8080\` (or update the Apollo Client configuration in \`src/services/apolloClient.ts\`).

### 2. Installation

1.  Navigate to the frontend directory:
    \`\`\`bash
    cd GroceriesExpirationApp/frontend
    \`\`\`
2.  Install dependencies:
    \`\`\`bash
    npm install
    # or
    yarn install
    \`\`\`

### 3. Running the App

You can run the app on an iOS simulator, Android emulator, or a physical device.

#### iOS

\`\`\`bash
npx react-native run-ios
\`\`\`

#### Android

\`\`\`bash
npx react-native run-android
\`\`\`

### 4. GraphQL Configuration

The Apollo Client is configured in \`src/services/apolloClient.ts\`. Ensure the \`uri\` points to your running backend:

\`\`\`typescript
// src/services/apolloClient.ts
const client = new ApolloClient({
  uri: 'http://localhost:8080/graphql', // <-- Check this port
  cache: new InMemoryCache(),
});
\`\`\`

### 5. Testing

The frontend uses Jest for unit and component testing.

\`\`\`bash
npm test
\`\`\`

### 6. Key Screens Implemented

*   **HomeScreen**: Displays the list of items in the household.
*   **AddItemScreen**: Form for manual item entry and **OCR simulation**.
*   **ItemDetailScreen**: View/Edit/Delete item details, and **Mark as Used** (sustainability).
*   **ARViewScreen**: Conceptual screen for **AR Fridge Navigation**.
*   **ImpactDashboardScreen**: Displays **CO₂ Savings** and gamification placeholders.
*   **SmartApplianceScreen**: Conceptual screen for **IoT Sync**.
*   **ShoppingListScreen**: Displays the **Smart Shopping List** and suggestions.
\`\`\`
