# Nutrition & Allergen Tracking Feature

## 🎯 Feature Overview

The **Nutrition & Allergen Tracking** feature transforms the Groceries Expiration Tracking App from a simple expiration tracker into a comprehensive **health and wellness platform**. This feature enables users to:

1. **Scan Nutrition Labels** - Extract complete nutritional information from product labels
2. **Parse Ingredients** - Automatically identify ingredients and detect allergens
3. **Manage Allergens** - Set personal allergens and dietary restrictions
4. **Get Smart Alerts** - Receive warnings when scanning items with allergens
5. **Track Consumption** - Log what you eat and track nutritional intake
6. **View Insights** - Analyze dietary patterns and nutritional goals

---

## 🏗️ Architecture

### Database Schema

The feature adds **9 new tables** to the database:

| Table | Purpose |
|-------|---------|
| `nutrition_info` | Stores complete nutritional information for items |
| `ingredients` | Master list of ingredients with allergen flags |
| `item_ingredients` | Many-to-many relationship between items and ingredients |
| `user_allergens` | User's allergens and severity levels |
| `user_dietary_preferences` | User's dietary restrictions and nutritional goals |
| `allergen_alerts` | Log of allergen warnings shown to users |
| `consumption_log` | Records of what users have consumed |
| `daily_nutrition_summary` | Daily aggregated nutrition totals |

### Backend Components

#### Models (Java Entities)

1. **NutritionInfo.java** - Complete nutrition facts
   - Serving information
   - Calories and macronutrients
   - Vitamins and minerals
   - 30+ nutritional fields

2. **Ingredient.java** - Ingredient master data
   - Name and description
   - Allergen flags (12 common allergens)
   - Dietary flags (vegan, vegetarian, gluten-free, etc.)
   - Aliases for matching

3. **UserAllergen.java** - User's allergens
   - Allergen type
   - Severity (mild, moderate, severe, life-threatening)
   - Notes

4. **UserDietaryPreferences.java** - User's dietary settings
   - 10 dietary restrictions (vegan, keto, etc.)
   - Daily nutritional goals
   - Ingredients to avoid/prefer

5. **ConsumptionLog.java** - Consumption records
   - Item consumed
   - Servings
   - Calculated nutritional totals
   - Meal type (breakfast, lunch, dinner, snack)

6. **DailyNutritionSummary.java** - Daily aggregates
   - Total calories, protein, carbs, fat
   - Meal breakdown
   - Goal achievement tracking

7. **AllergenAlert.java** - Allergen warnings
   - Alert details
   - User action (proceeded, cancelled, removed)
   - Status tracking

#### Services

**NutritionService.java** - Core business logic

Key Methods:
- `parseNutritionLabel(String ocrText)` - Extract nutrition from OCR text
- `parseIngredientsList(String ocrText)` - Extract ingredients from OCR text
- `checkForAllergens(User, Item, List<Ingredient>)` - Detect allergens
- `checkDietaryRestrictions(User, List<Ingredient>)` - Check dietary compliance
- `logConsumption(User, Item, servings, mealType)` - Record consumption
- `getDailySummary(User, date)` - Get daily nutrition totals
- `getNutritionInsights(User, startDate, endDate)` - Analyze patterns

---

## 📱 User Flow

### 1. Initial Setup

**User sets allergens and dietary preferences:**

```
Settings → Allergens & Diet
├── Add Allergens
│   ├── Select allergen type (milk, eggs, nuts, etc.)
│   ├── Set severity (mild, moderate, severe, life-threatening)
│   └── Add notes
├── Set Dietary Restrictions
│   ├── Vegan, Vegetarian, Gluten-Free, etc.
│   └── Keto, Paleo, Low-Carb, etc.
└── Set Nutritional Goals
    ├── Daily calorie goal
    ├── Daily protein goal
    ├── Daily carb goal
    └── Daily fat goal
```

### 2. Scanning Products

**User scans a product label:**

```
Scan Product
├── Take photo of nutrition label
├── OCR extracts text
├── Parse nutrition information
│   ├── Calories
│   ├── Macronutrients (protein, carbs, fat)
│   ├── Vitamins and minerals
│   └── Serving size
├── Parse ingredients list
│   ├── Extract individual ingredients
│   ├── Match against ingredient database
│   └── Identify allergens
├── Check for allergens
│   ├── Compare against user's allergens
│   └── Show alert if match found
└── Check dietary restrictions
    ├── Compare against user's diet
    └── Show warning if violation
```

### 3. Allergen Alert

**If allergen detected:**

```
⚠️ ALLERGEN ALERT

This product contains: Milk

Your allergen: Milk (Dairy)
Severity: Moderate

Actions:
├── View Details → Show full ingredient list
├── Proceed Anyway → Add item with warning
└── Cancel → Don't add item
```

### 4. Viewing Nutrition

**User views item nutrition:**

```
Item Details
├── Nutrition Facts
│   ├── Calories: 250
│   ├── Protein: 8g
│   ├── Carbs: 35g
│   ├── Fat: 9g
│   └── [View Full Nutrition]
├── Ingredients
│   ├── Wheat flour
│   ├── Sugar
│   ├── Milk ⚠️ (Allergen)
│   └── Eggs ⚠️ (Allergen)
└── Dietary Info
    ├── ✅ Vegetarian
    ├── ❌ Vegan (contains milk, eggs)
    └── ❌ Gluten-Free (contains wheat)
```

### 5. Logging Consumption

**User logs eating an item:**

```
Log Consumption
├── Select item
├── Enter servings (1.0)
├── Select meal type (Breakfast, Lunch, Dinner, Snack)
├── Add notes (optional)
└── Save
    ├── Calculate nutritional totals
    ├── Update daily summary
    └── Check against daily goals
```

### 6. Viewing Insights

**User views nutrition insights:**

```
Nutrition Dashboard
├── Today's Summary
│   ├── Calories: 1,850 / 2,000 (93%)
│   ├── Protein: 75g / 80g (94%)
│   ├── Carbs: 200g / 250g (80%)
│   └── Fat: 65g / 70g (93%)
├── Meal Breakdown
│   ├── Breakfast: 450 cal
│   ├── Lunch: 600 cal
│   ├── Dinner: 650 cal
│   └── Snacks: 150 cal
├── Weekly Average
│   ├── Avg Calories: 1,920
│   ├── Avg Protein: 78g
│   └── Goal Achievement: 85%
└── Consumption History
    └── [List of recent meals]
```

---

## 🔧 Implementation Status

### ✅ Completed

1. **Database Schema** (100%)
   - All 9 tables created
   - Indexes and foreign keys configured
   - Views for common queries
   - Seed data for common allergens

2. **Backend Models** (100%)
   - All 7 Java entities created
   - Relationships configured
   - Getters/setters implemented
   - Lifecycle callbacks (@PrePersist, @PreUpdate)

3. **NutritionService** (100%)
   - OCR parsing for nutrition labels
   - Ingredient extraction and matching
   - Allergen detection logic
   - Dietary restriction checking
   - Consumption logging
   - Daily summary aggregation
   - Insights calculation

### 🔄 In Progress

4. **Repositories** (Needed)
   - NutritionInfoRepository
   - IngredientRepository
   - UserAllergenRepository
   - UserDietaryPreferencesRepository
   - AllergenAlertRepository
   - ConsumptionLogRepository
   - DailyNutritionSummaryRepository
   - ItemIngredientRepository

5. **GraphQL API** (Needed)
   - Queries for nutrition data
   - Mutations for allergen management
   - Mutations for consumption logging
   - Subscriptions for real-time alerts

6. **Enhanced OCR** (Needed)
   - Improved nutrition label recognition
   - Ingredient list extraction
   - Multi-language support

7. **Frontend Screens** (Needed)
   - Allergen management screen
   - Dietary preferences screen
   - Nutrition detail screen
   - Consumption log screen
   - Nutrition insights dashboard
   - Allergen alert dialog

8. **Alert System** (Needed)
   - Real-time allergen warnings
   - Dietary violation notifications
   - Goal achievement notifications

---

## 📋 Next Steps

### Phase 1: Complete Backend (Estimated: 2-3 hours)

1. **Create Repositories**
   ```java
   @Repository
   public interface NutritionInfoRepository extends JpaRepository<NutritionInfo, Long> {
       NutritionInfo findByItemId(Long itemId);
   }
   
   @Repository
   public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
       Ingredient findByNameIgnoreCase(String name);
       List<Ingredient> findByIsAllergenTrue();
   }
   
   // ... 5 more repositories
   ```

2. **Create GraphQL Schema**
   ```graphql
   type NutritionInfo {
       id: ID!
       calories: Int
       protein: Float
       carbs: Float
       fat: Float
       # ... all nutrition fields
   }
   
   type Ingredient {
       id: ID!
       name: String!
       isAllergen: Boolean!
       allergenType: AllergenType
       # ... all ingredient fields
   }
   
   type Mutation {
       addUserAllergen(allergenType: AllergenType!, severity: Severity!): UserAllergen
       updateDietaryPreferences(input: DietaryPreferencesInput!): UserDietaryPreferences
       logConsumption(itemId: ID!, servings: Float!, mealType: MealType!): ConsumptionLog
       scanNutritionLabel(image: Upload!): NutritionScanResult
   }
   
   type Query {
       getUserAllergens: [UserAllergen]
       getDietaryPreferences: UserDietaryPreferences
       getDailySummary(date: Date!): DailyNutritionSummary
       getNutritionInsights(startDate: Date!, endDate: Date!): NutritionInsights
   }
   ```

3. **Create GraphQL Resolvers**
   ```java
   @Controller
   public class NutritionController {
       @Autowired
       private NutritionService nutritionService;
       
       @QueryMapping
       public List<UserAllergen> getUserAllergens(@AuthenticationPrincipal User user) {
           return userAllergenRepository.findByUserId(user.getId());
       }
       
       @MutationMapping
       public UserAllergen addUserAllergen(
           @AuthenticationPrincipal User user,
           @Argument AllergenType allergenType,
           @Argument Severity severity
       ) {
           // Implementation
       }
       
       // ... more resolvers
   }
   ```

### Phase 2: Enhance OCR (Estimated: 3-4 hours)

1. **Improve Nutrition Label Recognition**
   - Train OCR on nutrition label formats
   - Handle different label layouts (US, EU, etc.)
   - Extract structured data from free text

2. **Add Ingredient List Extraction**
   - Detect "Ingredients:" section
   - Parse comma-separated lists
   - Handle parentheses and sub-ingredients
   - Match against ingredient database

3. **Multi-Language Support**
   - Support nutrition labels in 20 languages
   - Translate ingredient names
   - Handle regional variations

### Phase 3: Build Frontend (Estimated: 6-8 hours)

1. **Allergen Management Screen**
   ```tsx
   // AllergenManagementScreen.tsx
   - List of user's allergens
   - Add allergen button
   - Edit severity
   - Delete allergen
   - Search common allergens
   ```

2. **Dietary Preferences Screen**
   ```tsx
   // DietaryPreferencesScreen.tsx
   - Dietary restriction toggles
   - Nutritional goal inputs
   - Save preferences button
   ```

3. **Nutrition Detail Screen**
   ```tsx
   // NutritionDetailScreen.tsx
   - Nutrition facts table
   - Ingredients list with allergen highlights
   - Dietary compatibility indicators
   - Consumption log button
   ```

4. **Consumption Log Screen**
   ```tsx
   // ConsumptionLogScreen.tsx
   - Item selector
   - Servings input
   - Meal type selector
   - Notes field
   - Save button
   ```

5. **Nutrition Insights Dashboard**
   ```tsx
   // NutritionDashboardScreen.tsx
   - Today's summary (calories, macros)
   - Progress bars for goals
   - Meal breakdown chart
   - Weekly trends graph
   - Consumption history list
   ```

6. **Allergen Alert Dialog**
   ```tsx
   // AllergenAlertDialog.tsx
   - Warning icon
   - Allergen name and severity
   - Ingredient list
   - Action buttons (proceed, cancel)
   ```

### Phase 4: Testing (Estimated: 2-3 hours)

1. **Unit Tests**
   - Test nutrition parsing
   - Test allergen detection
   - Test dietary checking
   - Test consumption logging

2. **Integration Tests**
   - Test end-to-end scanning flow
   - Test allergen alert flow
   - Test consumption tracking flow

3. **User Testing**
   - Test with real nutrition labels
   - Test with various allergens
   - Test with different diets
   - Collect feedback

---

## 🎨 UI/UX Design Guidelines

### Color Coding

**Allergen Severity:**
- 🟢 Mild: Green (#4CAF50)
- 🟡 Moderate: Yellow (#FFC107)
- 🟠 Severe: Orange (#FF9800)
- 🔴 Life-Threatening: Red (#F44336)

**Dietary Compliance:**
- ✅ Compliant: Green (#4CAF50)
- ⚠️ Warning: Yellow (#FFC107)
- ❌ Violation: Red (#F44336)

**Goal Progress:**
- < 50%: Red
- 50-80%: Yellow
- 80-100%: Green
- > 100%: Blue (exceeded)

### Icons

- 🥛 Milk/Dairy
- 🥚 Eggs
- 🐟 Fish
- 🦐 Shellfish
- 🌰 Tree Nuts
- 🥜 Peanuts
- 🌾 Wheat/Gluten
- 🫘 Soy/Soybeans
- ⚠️ Allergen Warning
- 🍽️ Meal/Consumption
- 📊 Nutrition/Analytics

### Accessibility

- High contrast mode for allergen alerts
- Screen reader support for all nutrition data
- Voice announcements for allergen warnings
- Large text mode for nutrition labels
- Color blind friendly indicators (use icons + colors)

---

## 📊 Database Schema Details

### nutrition_info

```sql
CREATE TABLE nutrition_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT NOT NULL,
    
    -- Serving
    serving_size VARCHAR(100),
    serving_unit VARCHAR(50),
    servings_per_container DECIMAL(10,2),
    
    -- Calories
    calories INT,
    calories_from_fat INT,
    
    -- Macros (grams)
    total_fat DECIMAL(10,2),
    saturated_fat DECIMAL(10,2),
    trans_fat DECIMAL(10,2),
    cholesterol DECIMAL(10,2), -- mg
    sodium DECIMAL(10,2), -- mg
    total_carbohydrates DECIMAL(10,2),
    dietary_fiber DECIMAL(10,2),
    total_sugars DECIMAL(10,2),
    added_sugars DECIMAL(10,2),
    protein DECIMAL(10,2),
    
    -- Vitamins & Minerals
    vitamin_a DECIMAL(10,2),
    vitamin_c DECIMAL(10,2),
    vitamin_d DECIMAL(10,2),
    calcium DECIMAL(10,2),
    iron DECIMAL(10,2),
    potassium DECIMAL(10,2),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);
```

### ingredients

```sql
CREATE TABLE ingredients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    
    -- Allergen flags
    is_allergen BOOLEAN DEFAULT FALSE,
    allergen_type ENUM('MILK', 'EGGS', 'FISH', 'SHELLFISH', 'TREE_NUTS', 
                       'PEANUTS', 'WHEAT', 'SOYBEANS', 'SESAME', 'GLUTEN', 
                       'CORN', 'SOY'),
    
    -- Dietary flags
    is_vegan BOOLEAN DEFAULT TRUE,
    is_vegetarian BOOLEAN DEFAULT TRUE,
    is_gluten_free BOOLEAN DEFAULT TRUE,
    is_dairy_free BOOLEAN DEFAULT TRUE,
    is_nut_free BOOLEAN DEFAULT TRUE,
    
    -- Aliases (JSON array)
    aliases JSON,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### user_allergens

```sql
CREATE TABLE user_allergens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    allergen_type ENUM('MILK', 'EGGS', 'FISH', 'SHELLFISH', 'TREE_NUTS', 
                       'PEANUTS', 'WHEAT', 'SOYBEANS', 'SESAME', 'GLUTEN', 
                       'CORN', 'SOY', 'CUSTOM') NOT NULL,
    custom_allergen_name VARCHAR(255),
    severity ENUM('MILD', 'MODERATE', 'SEVERE', 'LIFE_THREATENING') DEFAULT 'MODERATE',
    notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

## 🔐 Security Considerations

1. **Data Privacy**
   - Allergen data is highly sensitive (health information)
   - Encrypt allergen and dietary data at rest
   - Use HTTPS for all API calls
   - Implement proper access controls

2. **Input Validation**
   - Validate all nutrition values (reasonable ranges)
   - Sanitize ingredient names
   - Prevent SQL injection in ingredient search

3. **Rate Limiting**
   - Limit OCR API calls per user
   - Prevent abuse of nutrition lookup

4. **User Consent**
   - Clear privacy policy for health data
   - Opt-in for data collection
   - Allow data export and deletion

---

## 📈 Performance Optimization

1. **Database Indexing**
   - Index on `item_id` in nutrition_info
   - Index on `user_id` in user_allergens
   - Index on `allergen_type` in ingredients
   - Index on `consumed_at` in consumption_log

2. **Caching**
   - Cache ingredient database in memory
   - Cache user allergens and preferences
   - Cache daily summaries

3. **Batch Processing**
   - Batch ingredient lookups
   - Aggregate daily summaries in background job
   - Lazy load nutrition details

---

## 🎯 Success Metrics

1. **Adoption**
   - % of users who set allergens
   - % of users who log consumption
   - % of items with nutrition data

2. **Engagement**
   - Daily active users viewing nutrition
   - Average consumption logs per day
   - Allergen alerts shown vs. heeded

3. **Health Impact**
   - Users meeting daily goals
   - Reduction in allergen exposure
   - Improved dietary compliance

---

## 🚀 Future Enhancements

1. **Barcode Nutrition Lookup**
   - Integrate with nutrition databases (USDA, Open Food Facts)
   - Auto-populate nutrition from barcode

2. **Recipe Analysis**
   - Scan recipe ingredients
   - Calculate total nutrition for recipes
   - Detect allergens in recipes

3. **Meal Planning**
   - Suggest meals based on goals
   - Generate shopping lists from meal plans
   - Track meal prep

4. **AI Nutrition Coach**
   - Personalized recommendations
   - Identify nutritional gaps
   - Suggest healthier alternatives

5. **Integration with Fitness Apps**
   - Sync with Apple Health / Google Fit
   - Track calories burned
   - Adjust goals based on activity

6. **Social Features**
   - Share recipes
   - Compare nutrition with friends
   - Group challenges

---

## 📚 Resources

### Nutrition Databases

- [USDA FoodData Central](https://fdc.nal.usda.gov/)
- [Open Food Facts](https://world.openfoodfacts.org/)
- [Nutritionix API](https://www.nutritionix.com/business/api)

### Allergen Information

- [FDA Food Allergen Labeling](https://www.fda.gov/food/food-labeling-nutrition/food-allergen-labeling)
- [FARE (Food Allergy Research & Education)](https://www.foodallergy.org/)

### OCR Libraries

- [Tesseract OCR](https://github.com/tesseract-ocr/tesseract)
- [Google Cloud Vision API](https://cloud.google.com/vision)
- [AWS Textract](https://aws.amazon.com/textract/)

---

## 📝 Summary

The **Nutrition & Allergen Tracking** feature is a **game-changing addition** that transforms your app from a simple expiration tracker into a **comprehensive health and wellness platform**. 

**What's Been Built:**
✅ Complete database schema (9 tables)
✅ All Java models (7 entities)
✅ Core business logic (NutritionService)
✅ Allergen detection system
✅ Consumption tracking system
✅ Nutrition insights analytics

**What's Next:**
🔄 Create repositories (7 interfaces)
🔄 Build GraphQL API (queries, mutations, subscriptions)
🔄 Enhance OCR for nutrition labels
🔄 Build frontend screens (6 screens)
🔄 Implement alert system
🔄 Add comprehensive testing

**Estimated Total Time:** 13-18 hours for complete implementation

**Impact:** This feature will make your app **stand out** in the market and provide **real value** to users managing allergies, dietary restrictions, and health goals.

---

**Last Updated:** November 6, 2025  
**Version:** 1.0.0  
**Status:** 🔄 **IN PROGRESS** (Backend 60% complete)
