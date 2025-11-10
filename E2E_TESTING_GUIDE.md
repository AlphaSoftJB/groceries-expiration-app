# End-to-End Testing Guide
## Groceries Expiration Tracking App - Nutrition & Allergen Features

This document provides comprehensive end-to-end testing scenarios for the nutrition and allergen tracking features.

---

## Table of Contents
1. [Test Environment Setup](#test-environment-setup)
2. [Core Workflows](#core-workflows)
3. [Manual Testing Scenarios](#manual-testing-scenarios)
4. [API Testing with GraphQL](#api-testing-with-graphql)
5. [Expected Results](#expected-results)

---

## Test Environment Setup

### Prerequisites
- Java backend running on `http://localhost:8080`
- Web frontend running on `http://localhost:3000`
- Test database with clean state
- GraphQL Playground available at `http://localhost:8080/graphiql`

### Test User Setup
```graphql
mutation {
  createUser(input: {
    email: "testuser@example.com"
    password: "Test123!"
    name: "Test User"
    householdName: "Test Household"
  }) {
    id
    email
    name
  }
}
```

---

## Core Workflows

### Workflow 1: Allergen Detection End-to-End

**Scenario**: User with milk allergy scans a product containing milk and receives an alert

**Steps**:

1. **Add User Allergen**
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

2. **Create Item**
   ```graphql
   mutation {
     createItem(input: {
       name: "Whole Milk"
       barcode: "012345678901"
       quantity: 1
       expirationDate: "2025-11-20"
       storageLocation: "Refrigerator"
       householdId: "1"
     }) {
       id
       name
     }
   }
   ```

3. **Scan Nutrition Label** (includes ingredient parsing)
   ```graphql
   mutation {
     scanNutritionLabel(itemId: "1", ocrText: """
       Nutrition Facts
       Serving Size: 1 cup (240ml)
       Calories: 150
       Total Fat: 8g
       Protein: 8g
       
       Ingredients: Milk, Vitamin D3
     """) {
       success
       message
       allergenAlerts {
         id
         allergenType
         severity
         status
       }
     }
   }
   ```

4. **Verify Alert Created**
   ```graphql
   query {
     getPendingAlerts {
       id
       item {
         name
       }
       allergenType
       severity
       status
     }
   }
   ```

5. **Acknowledge Alert**
   ```graphql
   mutation {
     acknowledgeAllergenAlert(alertId: "1", action: REMOVED_ITEM) {
       id
       status
       userAction
     }
   }
   ```

**Expected Results**:
- ✅ Allergen successfully added to user profile
- ✅ Item created with nutrition info
- ✅ Milk ingredient detected from OCR
- ✅ Allergen alert created with SEVERE severity
- ✅ Alert status changed to ACKNOWLEDGED after user action

---

### Workflow 2: Consumption Tracking and Daily Summary

**Scenario**: User logs meals throughout the day and views nutrition summary

**Steps**:

1. **Add Nutrition Info to Items**
   ```graphql
   mutation {
     addNutritionInfo(input: {
       itemId: "1"
       servingSize: "1 cup"
       servingUnit: "cup"
       servingsPerContainer: 2.0
       calories: 150
       totalFat: 8.0
       protein: 8.0
       totalCarbohydrates: 12.0
     }) {
       id
       calories
     }
   }
   ```

2. **Log Breakfast**
   ```graphql
   mutation {
     logConsumption(input: {
       itemId: "1"
       servingsConsumed: 1.0
       mealType: BREAKFAST
       notes: "With cereal"
     }) {
       id
       totalCalories
       totalProtein
       mealType
     }
   }
   ```

3. **Log Lunch**
   ```graphql
   mutation {
     logConsumption(input: {
       itemId: "2"
       servingsConsumed: 1.5
       mealType: LUNCH
     }) {
       id
       totalCalories
     }
   }
   ```

4. **View Today's Consumption**
   ```graphql
   query {
     getTodayConsumption {
       id
       item {
         name
       }
       servingsConsumed
       totalCalories
       totalProtein
       mealType
       consumedAt
     }
   }
   ```

5. **View Daily Summary**
   ```graphql
   query {
     getTodaySummary {
       date
       totalCalories
       totalProtein
       totalCarbs
       totalFat
       breakfastCalories
       lunchCalories
       dinnerCalories
       snackCalories
       calorieGoalMet
     }
   }
   ```

**Expected Results**:
- ✅ Consumption logs created for each meal
- ✅ Calories and macros calculated correctly (150 * servings)
- ✅ Daily summary aggregates all meals
- ✅ Meal-specific calorie breakdowns accurate
- ✅ Goal achievement status calculated

---

### Workflow 3: Dietary Preferences and Compliance

**Scenario**: Vegan user sets preferences and checks item compliance

**Steps**:

1. **Set Dietary Preferences**
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

2. **Scan Item with Non-Vegan Ingredients**
   ```graphql
   mutation {
     scanNutritionLabel(itemId: "3", ocrText: """
       Nutrition Facts
       Calories: 200
       
       Ingredients: Wheat flour, eggs, milk, sugar
     """) {
       success
       dietaryViolations
       ingredients {
         name
         isVegan
         isVegetarian
       }
     }
   }
   ```

3. **View Item Ingredients**
   ```graphql
   query {
     getItemIngredients(itemId: "3") {
       ingredient {
         name
         isVegan
         isVegetarian
         isGlutenFree
       }
     }
   }
   ```

**Expected Results**:
- ✅ Dietary preferences saved successfully
- ✅ Non-vegan ingredients (eggs, milk) detected
- ✅ Dietary violations reported
- ✅ Ingredient properties correctly set

---

### Workflow 4: Weekly Nutrition Insights

**Scenario**: User reviews weekly nutrition trends

**Steps**:

1. **Log Meals for 7 Days** (repeat for each day)
   ```graphql
   mutation {
     logConsumption(input: {
       itemId: "1"
       servingsConsumed: 1.0
       mealType: BREAKFAST
     }) {
       id
     }
   }
   ```

2. **View Weekly Summaries**
   ```graphql
   query {
     getCurrentWeekSummaries {
       date
       totalCalories
       totalProtein
       calorieGoalMet
     }
   }
   ```

3. **Get Weekly Insights**
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

**Expected Results**:
- ✅ 7 daily summaries created
- ✅ Average calculations accurate
- ✅ Goal achievement rate calculated (days met / total days)
- ✅ Trends visible across the week

---

## Manual Testing Scenarios

### Frontend Testing

#### Test 1: Nutrition Dashboard
1. Navigate to `/nutrition`
2. Verify today's calorie count displays
3. Check macronutrient progress bars
4. Verify meal breakdown (breakfast/lunch/dinner/snack)
5. Switch to "This Week" tab
6. Verify weekly averages display
7. Check goal achievement percentage

**Pass Criteria**: All data displays correctly, no loading errors

---

#### Test 2: Allergen Management
1. Navigate to `/allergens`
2. Click "Add Allergen"
3. Select allergen type (e.g., PEANUTS)
4. Select severity (e.g., LIFE_THREATENING)
5. Add notes
6. Click "Add Allergen"
7. Verify allergen card appears
8. Click delete icon
9. Confirm allergen removed

**Pass Criteria**: CRUD operations work smoothly, UI updates immediately

---

#### Test 3: Consumption Logging
1. Navigate to `/consumption`
2. Click "Log Meal"
3. Select food item from dropdown
4. Enter servings (e.g., 1.5)
5. Select meal type (BREAKFAST)
6. Add notes
7. Click "Log Meal"
8. Verify meal appears in appropriate section
9. Check calorie totals update

**Pass Criteria**: Meal logged successfully, totals calculated correctly

---

#### Test 4: Dietary Preferences
1. Navigate to `/preferences`
2. Toggle dietary restrictions (Vegan, Gluten-Free, etc.)
3. Set daily calorie goal (e.g., 2000)
4. Set protein goal (e.g., 50g)
5. Set carb and fat goals
6. Click "Save Preferences"
7. Refresh page
8. Verify preferences persisted

**Pass Criteria**: All settings save and persist across sessions

---

#### Test 5: Allergen Alerts
1. Navigate to `/alerts`
2. Verify pending alerts display
3. Click "I'll Proceed Anyway" on an alert
4. Verify alert moves to acknowledged
5. Switch to "Severe" tab
6. Verify life-threatening alerts highlighted
7. Click "Remove Item Immediately"
8. Verify alert status updates

**Pass Criteria**: Alert acknowledgment works, severity levels display correctly

---

#### Test 6: Item Nutrition Detail
1. Navigate to `/items/1/nutrition`
2. Verify nutrition facts label displays
3. Check calorie count
4. Verify macronutrient breakdown
5. Check % Daily Value calculations
6. Scroll to ingredients section
7. Verify allergen badges display
8. Check dietary compliance badges (Vegan, Gluten-Free)

**Pass Criteria**: FDA-style nutrition label renders correctly

---

## API Testing with GraphQL

### Using GraphQL Playground

1. Open `http://localhost:8080/graphiql`
2. Set authorization header:
   ```
   {
     "Authorization": "Bearer YOUR_JWT_TOKEN"
   }
   ```
3. Run queries from workflows above
4. Verify responses match expected schemas

### Using cURL

```bash
# Example: Get pending alerts
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "query": "query { getPendingAlerts { id allergenType severity } }"
  }'
```

---

## Expected Results Summary

### Success Criteria

| Feature | Test | Expected Result |
|---------|------|-----------------|
| Allergen Detection | Add allergen + scan item | Alert created with correct severity |
| Consumption Logging | Log 3 meals | Daily summary shows correct totals |
| Dietary Preferences | Set vegan + scan non-vegan item | Dietary violations detected |
| Weekly Insights | Log meals for 7 days | Accurate averages calculated |
| Alert Acknowledgment | Acknowledge pending alert | Status changes to ACKNOWLEDGED |
| Nutrition Facts | View item nutrition | FDA-style label displays |

### Performance Benchmarks

- GraphQL query response time: < 200ms
- Frontend page load time: < 1s
- Allergen detection: < 500ms
- Daily summary calculation: < 300ms

---

## Troubleshooting

### Common Issues

**Issue**: Allergen alert not created
- **Check**: User has allergen in profile
- **Check**: Item has ingredient with matching allergen type
- **Check**: NutritionService.detectAllergens() called

**Issue**: Daily summary not updating
- **Check**: Consumption logs have correct date
- **Check**: Daily summary exists for today
- **Check**: Totals calculated in consumption log

**Issue**: Dietary violations not detected
- **Check**: User has dietary preferences set
- **Check**: Ingredients have dietary properties set
- **Check**: Item has ingredients linked

---

## Test Data Setup Scripts

### Create Test User with Allergens
```graphql
mutation {
  createUser(input: {
    email: "test@example.com"
    password: "Test123!"
    name: "Test User"
    householdName: "Test Household"
  }) {
    id
  }
}

mutation {
  addUserAllergen(input: {
    allergenType: MILK
    severity: SEVERE
  }) { id }
}

mutation {
  addUserAllergen(input: {
    allergenType: PEANUTS
    severity: LIFE_THREATENING
  }) { id }
}
```

### Create Test Items with Nutrition
```graphql
mutation {
  createItem(input: {
    name: "Whole Milk"
    barcode: "111111111111"
    quantity: 1
    expirationDate: "2025-12-01"
    storageLocation: "Refrigerator"
    householdId: "1"
  }) {
    id
  }
}

mutation {
  addNutritionInfo(input: {
    itemId: "1"
    servingSize: "1 cup"
    calories: 150
    totalFat: 8.0
    protein: 8.0
    totalCarbohydrates: 12.0
  }) {
    id
  }
}
```

---

## Automated Testing Recommendations

For production deployment, implement:

1. **Unit Tests** - Test individual service methods
2. **Integration Tests** - Test repository + service interactions
3. **API Tests** - Test GraphQL endpoints with Postman/Newman
4. **E2E Tests** - Use Selenium/Cypress for frontend workflows
5. **Load Tests** - Use JMeter for performance testing

---

*Last Updated: November 9, 2025*
*Version: 1.0.0*
