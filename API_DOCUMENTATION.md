# Groceries Expiration Tracking App - GraphQL API Documentation

## Table of Contents
1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Core API](#core-api)
4. [Nutrition & Allergen API](#nutrition--allergen-api)
5. [Code Examples](#code-examples)
6. [Error Handling](#error-handling)

---

## Overview

The Groceries Expiration Tracking App provides a comprehensive GraphQL API for managing groceries, tracking nutrition, detecting allergens, and gamifying the food waste reduction experience.

**Base URL**: `http://localhost:8080/graphql`

**GraphQL Endpoint**: `/graphql`

**GraphQL Playground**: `http://localhost:8080/graphiql` (development only)

---

## Authentication

All mutations and most queries require authentication. The API uses JWT tokens for authentication.

### Headers
```http
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

---

## Core API

### User Management

#### Query: Get Current User
```graphql
query {
  me {
    id
    email
    name
    household {
      id
      name
    }
    createdAt
  }
}
```

#### Mutation: Create User
```graphql
mutation {
  createUser(input: {
    email: "user@example.com"
    password: "securePassword123"
    name: "John Doe"
    householdName: "Doe Family"
  }) {
    id
    email
    name
  }
}
```

---

### Item Management

#### Query: Get Items by Household
```graphql
query {
  itemsByHousehold(householdId: "1") {
    id
    name
    quantity
    expirationDate
    predictedExpirationDate
    storageLocation
    barcode
    household {
      id
      name
    }
    addedBy {
      id
      name
    }
  }
}
```

#### Query: Get Expiring Items
```graphql
query {
  expiringItems(householdId: "1", daysAhead: 7) {
    id
    name
    expirationDate
    quantity
  }
}
```

#### Mutation: Create Item
```graphql
mutation {
  createItem(input: {
    name: "Milk"
    barcode: "012345678901"
    quantity: 1
    expirationDate: "2025-11-16"
    storageLocation: "Refrigerator"
    householdId: "1"
  }) {
    id
    name
    expirationDate
  }
}
```

#### Mutation: Update Item
```graphql
mutation {
  updateItem(input: {
    itemId: "1"
    quantity: 2
    expirationDate: "2025-11-20"
  }) {
    id
    quantity
    expirationDate
  }
}
```

#### Mutation: Delete Item
```graphql
mutation {
  deleteItem(itemId: "1")
}
```

---

### OCR & Barcode Scanning

#### Mutation: Process Image for OCR
```graphql
mutation {
  processImageForOCR(imageBase64: "data:image/jpeg;base64,...") {
    name
    quantity
    expirationDate
  }
}
```

#### Mutation: Scan Barcode
```graphql
mutation {
  scanBarcode(barcode: "012345678901") {
    success
    message
    barcode
    name
    brand
    category
    imageUrl
    storageLocation
    estimatedShelfLifeDays
  }
}
```

---

### Gamification

#### Query: Get User Stats
```graphql
query {
  userStats(userId: "1") {
    level
    experiencePoints
    xpProgress
    xpNeeded
    totalPoints
    itemsSaved
    itemsScanned
    streak
    totalCo2Saved
    achievementsUnlocked
  }
}
```

#### Query: Get Leaderboard
```graphql
query {
  leaderboard(limit: 10) {
    userId
    name
    level
    experiencePoints
    itemsSaved
    totalCo2Saved
  }
}
```

#### Mutation: Award Experience
```graphql
mutation {
  awardExperience(userId: "1", xp: 50, reason: "Scanned 10 items") {
    oldLevel
    newLevel
    leveledUp
    xpGained
    totalXP
    reason
  }
}
```

---

### Shopping List

#### Query: Get Shopping List
```graphql
query {
  shoppingListByHousehold(householdId: "1") {
    id
    name
    household {
      id
      name
    }
    items {
      id
      name
      quantity
      isPurchased
    }
  }
}
```

#### Mutation: Add Item to Shopping List
```graphql
mutation {
  addItemToShoppingList(input: {
    listId: "1"
    name: "Eggs"
    quantity: 12
  }) {
    id
    name
    quantity
    isPurchased
  }
}
```

#### Mutation: Toggle Shopping List Item
```graphql
mutation {
  toggleShoppingListItem(itemId: "1") {
    id
    isPurchased
  }
}
```

---

### Sustainability

#### Query: Get Sustainability Metrics
```graphql
query {
  sustainabilityMetrics {
    totalCo2SavedKg
  }
}
```

---

## Nutrition & Allergen API

### Nutrition Information

#### Query: Get Item Nutrition
```graphql
query {
  getItemNutrition(itemId: "1") {
    id
    item {
      id
      name
    }
    servingSize
    servingUnit
    servingsPerContainer
    calories
    caloriesFromFat
    totalFat
    saturatedFat
    transFat
    cholesterol
    sodium
    totalCarbohydrates
    dietaryFiber
    totalSugars
    addedSugars
    protein
    vitaminD
    calcium
    iron
    potassium
  }
}
```

#### Mutation: Add Nutrition Info
```graphql
mutation {
  addNutritionInfo(input: {
    itemId: "1"
    servingSize: "1 cup"
    servingUnit: "cup"
    servingsPerContainer: 2.0
    calories: 150
    totalFat: 8.0
    saturatedFat: 5.0
    cholesterol: 30.0
    sodium: 125.0
    totalCarbohydrates: 12.0
    dietaryFiber: 0.0
    totalSugars: 12.0
    protein: 8.0
    vitaminD: 2.5
    calcium: 300.0
    iron: 0.0
    potassium: 380.0
  }) {
    id
    calories
    protein
  }
}
```

#### Mutation: Scan Nutrition Label
```graphql
mutation {
  scanNutritionLabel(itemId: "1", ocrText: """
    Nutrition Facts
    Serving Size: 1 cup (240ml)
    Calories: 150
    Total Fat: 8g
    Protein: 8g
    Total Carbohydrates: 12g
    Ingredients: Milk, Vitamin D3
  """) {
    success
    message
    nutritionInfo {
      id
      calories
      protein
    }
    ingredients {
      id
      name
      isAllergen
      allergenType
    }
    allergenAlerts {
      id
      allergenType
      severity
      status
    }
    dietaryViolations
  }
}
```

---

### Ingredients

#### Query: Get Item Ingredients
```graphql
query {
  getItemIngredients(itemId: "1") {
    id
    item {
      id
      name
    }
    ingredient {
      id
      name
      isAllergen
      allergenType
      isVegan
      isVegetarian
      isGlutenFree
    }
    position
    percentage
  }
}
```

#### Query: Search Ingredients
```graphql
query {
  searchIngredients(query: "milk") {
    id
    name
    isAllergen
    allergenType
    isVegan
    isVegetarian
  }
}
```

#### Mutation: Add Ingredient
```graphql
mutation {
  addIngredient(
    name: "Almond Milk"
    isAllergen: true
    allergenType: TREE_NUTS
  ) {
    id
    name
    isAllergen
    allergenType
  }
}
```

---

### User Allergens

#### Query: Get User Allergens
```graphql
query {
  getUserAllergens {
    id
    user {
      id
      name
    }
    allergenType
    customAllergenName
    severity
    notes
  }
}
```

#### Query: Get Severe Allergens
```graphql
query {
  getSevereAllergens {
    id
    allergenType
    severity
  }
}
```

#### Mutation: Add User Allergen
```graphql
mutation {
  addUserAllergen(input: {
    allergenType: MILK
    severity: SEVERE
    notes: "Lactose intolerant"
  }) {
    id
    allergenType
    severity
  }
}
```

#### Mutation: Remove User Allergen
```graphql
mutation {
  removeUserAllergen(id: "1")
}
```

---

### Allergen Alerts

#### Query: Get Pending Alerts
```graphql
query {
  getPendingAlerts {
    id
    user {
      id
      name
    }
    item {
      id
      name
    }
    allergenType
    allergenName
    severity
    status
    createdAt
  }
}
```

#### Query: Get Severe Alerts
```graphql
query {
  getSevereAlerts {
    id
    allergenType
    severity
    status
  }
}
```

#### Mutation: Acknowledge Alert
```graphql
mutation {
  acknowledgeAllergenAlert(alertId: "1", action: PROCEEDED) {
    id
    status
    userAction
    userActionAt
  }
}
```

---

### Dietary Preferences

#### Query: Get Dietary Preferences
```graphql
query {
  getDietaryPreferences {
    id
    user {
      id
      name
    }
    isVegan
    isVegetarian
    isGlutenFree
    isDairyFree
    isNutFree
    isKosher
    isHalal
    isLowCarb
    isKeto
    isPaleo
    dailyCalorieGoal
    dailyProteinGoal
    dailyCarbGoal
    dailyFatGoal
  }
}
```

#### Mutation: Update Dietary Preferences
```graphql
mutation {
  updateDietaryPreferences(input: {
    isVegan: true
    isGlutenFree: true
    dailyCalorieGoal: 2000
    dailyProteinGoal: 50.0
    dailyCarbGoal: 250.0
    dailyFatGoal: 70.0
  }) {
    id
    isVegan
    isGlutenFree
    dailyCalorieGoal
  }
}
```

---

### Consumption Tracking

#### Query: Get Today's Consumption
```graphql
query {
  getTodayConsumption {
    id
    item {
      id
      name
    }
    consumedAt
    servingsConsumed
    totalCalories
    totalProtein
    totalCarbs
    totalFat
    mealType
  }
}
```

#### Query: Get Consumption by Date Range
```graphql
query {
  getConsumptionByDateRange(
    startDate: "2025-11-01"
    endDate: "2025-11-30"
  ) {
    id
    consumedAt
    totalCalories
    mealType
  }
}
```

#### Mutation: Log Consumption
```graphql
mutation {
  logConsumption(input: {
    itemId: "1"
    servingsConsumed: 1.5
    mealType: BREAKFAST
    notes: "Had with cereal"
  }) {
    id
    totalCalories
    totalProtein
    mealType
  }
}
```

---

### Daily Nutrition Summaries

#### Query: Get Today's Summary
```graphql
query {
  getTodaySummary {
    id
    date
    totalCalories
    totalProtein
    totalCarbs
    totalFat
    totalFiber
    totalSugar
    totalSodium
    breakfastCalories
    lunchCalories
    dinnerCalories
    snackCalories
    calorieGoalMet
    proteinGoalMet
  }
}
```

#### Query: Get Current Week Summaries
```graphql
query {
  getCurrentWeekSummaries {
    id
    date
    totalCalories
    totalProtein
    calorieGoalMet
  }
}
```

---

### Nutrition Insights

#### Query: Get Weekly Insights
```graphql
query {
  getWeeklyInsights {
    avgCalories
    avgProtein
    avgCarbs
    avgFat
    goalAchievementRate
    totalDays
  }
}
```

#### Query: Get Custom Date Range Insights
```graphql
query {
  getNutritionInsights(
    startDate: "2025-11-01"
    endDate: "2025-11-30"
  ) {
    avgCalories
    avgProtein
    avgCarbs
    avgFat
    goalAchievementRate
    totalDays
  }
}
```

---

## Code Examples

### JavaScript/TypeScript (Apollo Client)

```typescript
import { ApolloClient, InMemoryCache, gql } from '@apollo/client';

const client = new ApolloClient({
  uri: 'http://localhost:8080/graphql',
  cache: new InMemoryCache(),
  headers: {
    Authorization: `Bearer ${yourJwtToken}`
  }
});

// Query example
const GET_USER_ALLERGENS = gql`
  query GetUserAllergens {
    getUserAllergens {
      id
      allergenType
      severity
    }
  }
`;

const { data } = await client.query({
  query: GET_USER_ALLERGENS
});

// Mutation example
const ADD_ALLERGEN = gql`
  mutation AddUserAllergen($input: UserAllergenInput!) {
    addUserAllergen(input: $input) {
      id
      allergenType
      severity
    }
  }
`;

const { data } = await client.mutate({
  mutation: ADD_ALLERGEN,
  variables: {
    input: {
      allergenType: 'MILK',
      severity: 'SEVERE'
    }
  }
});
```

### React Native Example

```typescript
import { useQuery, useMutation } from '@apollo/client';

function NutritionScreen() {
  const { data, loading } = useQuery(GET_ITEM_NUTRITION, {
    variables: { itemId: '1' }
  });

  const [logConsumption] = useMutation(LOG_CONSUMPTION);

  const handleLogMeal = async () => {
    await logConsumption({
      variables: {
        input: {
          itemId: '1',
          servingsConsumed: 1.0,
          mealType: 'BREAKFAST'
        }
      }
    });
  };

  if (loading) return <Loading />;

  return (
    <View>
      <Text>Calories: {data.getItemNutrition.calories}</Text>
      <Button onPress={handleLogMeal} title="Log Meal" />
    </View>
  );
}
```

---

## Error Handling

### Common Error Codes

| Code | Description | Solution |
|------|-------------|----------|
| `UNAUTHENTICATED` | No valid JWT token | Include Authorization header |
| `FORBIDDEN` | Insufficient permissions | Check user role/permissions |
| `NOT_FOUND` | Resource not found | Verify ID exists |
| `BAD_USER_INPUT` | Invalid input data | Check input validation |
| `INTERNAL_SERVER_ERROR` | Server error | Contact support |

### Error Response Format

```json
{
  "errors": [
    {
      "message": "User allergen not found",
      "locations": [{"line": 2, "column": 3}],
      "path": ["removeUserAllergen"],
      "extensions": {
        "code": "NOT_FOUND",
        "classification": "DataFetchingException"
      }
    }
  ],
  "data": null
}
```

---

## Enums Reference

### AllergenType
```
MILK, EGGS, FISH, SHELLFISH, TREE_NUTS, PEANUTS, 
WHEAT, SOYBEANS, SESAME, GLUTEN, CORN, SOY, 
CUSTOM, DIETARY_RESTRICTION
```

### Severity
```
MILD, MODERATE, SEVERE, LIFE_THREATENING
```

### MealType
```
BREAKFAST, LUNCH, DINNER, SNACK, OTHER
```

### AlertStatus
```
PENDING, ACKNOWLEDGED, IGNORED, ITEM_REMOVED
```

### UserAction
```
PROCEEDED, CANCELLED, REMOVED_ITEM
```

---

## Rate Limiting

- **Queries**: 100 requests per minute per user
- **Mutations**: 50 requests per minute per user
- **Subscriptions**: 10 concurrent connections per user

---

## Best Practices

1. **Use Fragments** for reusable field selections
2. **Batch Queries** when fetching multiple resources
3. **Use Variables** instead of string interpolation
4. **Implement Pagination** for large result sets
5. **Cache Responses** to reduce network calls
6. **Handle Errors Gracefully** with try-catch blocks
7. **Use Subscriptions** for real-time allergen alerts

---

## Support

For API support, please contact:
- **Email**: support@groceriesapp.com
- **Documentation**: https://docs.groceriesapp.com
- **GitHub**: https://github.com/groceriesapp/api

---

*Last Updated: November 9, 2025*
*API Version: 1.0.0*
