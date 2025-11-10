# Nutrition & Allergen Tracking - Implementation TODO

## ✅ Completed

- [x] Design database schema (9 tables)
- [x] Create migration script (V2__nutrition_allergen_schema.sql)
- [x] Create NutritionInfo model
- [x] Create Ingredient model
- [x] Create UserAllergen model
- [x] Create UserDietaryPreferences model
- [x] Create ConsumptionLog model
- [x] Create DailyNutritionSummary model
- [x] Create AllergenAlert model
- [x] Create NutritionService with core business logic

## 🔄 Backend (Phase 1)

- [ ] Create NutritionInfoRepository
- [ ] Create IngredientRepository
- [ ] Create UserAllergenRepository
- [ ] Create UserDietaryPreferencesRepository
- [ ] Create AllergenAlertRepository
- [ ] Create ConsumptionLogRepository
- [ ] Create DailyNutritionSummaryRepository
- [ ] Create ItemIngredientRepository
- [ ] Create GraphQL schema for nutrition types
- [ ] Create GraphQL queries (getUserAllergens, getDietaryPreferences, etc.)
- [ ] Create GraphQL mutations (addUserAllergen, logConsumption, etc.)
- [ ] Create GraphQL resolvers/controllers
- [ ] Add unit tests for NutritionService
- [ ] Add integration tests for GraphQL API

## 🔄 OCR Enhancement (Phase 2)

- [ ] Enhance OCR service for nutrition label recognition
- [ ] Add ingredient list extraction
- [ ] Add multi-language support for nutrition labels
- [ ] Add barcode nutrition lookup integration
- [ ] Test OCR with real nutrition labels

## 🔄 Frontend (Phase 3)

- [ ] Create AllergenManagementScreen.tsx
- [ ] Create DietaryPreferencesScreen.tsx
- [ ] Create NutritionDetailScreen.tsx
- [ ] Create ConsumptionLogScreen.tsx
- [ ] Create NutritionDashboardScreen.tsx
- [ ] Create AllergenAlertDialog.tsx
- [ ] Add GraphQL queries to frontend
- [ ] Add GraphQL mutations to frontend
- [ ] Add navigation routes for new screens
- [ ] Add icons and styling
- [ ] Add accessibility features
- [ ] Add i18n translations for nutrition features

## 🔄 Testing (Phase 4)

- [ ] Test nutrition label scanning
- [ ] Test allergen detection
- [ ] Test dietary restriction checking
- [ ] Test consumption logging
- [ ] Test daily summary calculation
- [ ] Test nutrition insights
- [ ] User testing with real products
- [ ] Fix bugs and polish UI

## 📚 Documentation

- [ ] Update README with nutrition features
- [ ] Create user guide for allergen management
- [ ] Create developer guide for nutrition API
- [ ] Add API documentation
- [ ] Create video tutorial

## 🚀 Deployment

- [ ] Run database migration
- [ ] Deploy backend changes
- [ ] Deploy frontend changes
- [ ] Test in production
- [ ] Monitor for issues
