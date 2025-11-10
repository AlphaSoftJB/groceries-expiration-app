import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import { useQuery } from '@apollo/client';
import {
  GET_ITEM_NUTRITION,
  GET_ITEM_INGREDIENTS,
  NutritionInfo,
  ItemIngredient,
} from '../graphql/nutritionQueries';
import { useTranslation } from 'react-i18next';

interface NutritionFactsDisplayProps {
  itemId: string;
}

/**
 * Nutrition Facts Display Component
 * Shows complete nutrition information and ingredients for an item
 * Minimal UI Implementation
 */
const NutritionFactsDisplay: React.FC<NutritionFactsDisplayProps> = ({ itemId }) => {
  const { t } = useTranslation();

  const { data: nutritionData, loading: nutritionLoading } = useQuery(GET_ITEM_NUTRITION, {
    variables: { itemId },
    skip: !itemId,
  });

  const { data: ingredientsData, loading: ingredientsLoading } = useQuery(GET_ITEM_INGREDIENTS, {
    variables: { itemId },
    skip: !itemId,
  });

  const nutritionInfo: NutritionInfo | null = nutritionData?.getItemNutrition;
  const ingredients: ItemIngredient[] = ingredientsData?.getItemIngredients || [];

  if (nutritionLoading || ingredientsLoading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#007AFF" />
      </View>
    );
  }

  if (!nutritionInfo && ingredients.length === 0) {
    return (
      <View style={styles.emptyState}>
        <Text style={styles.emptyIcon}>📊</Text>
        <Text style={styles.emptyText}>{t('no_nutrition_info')}</Text>
        <Text style={styles.emptySubtext}>
          {t('scan_nutrition_label_to_add_info')}
        </Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      {/* Nutrition Facts */}
      {nutritionInfo && (
        <View style={styles.nutritionFacts}>
          <Text style={styles.sectionTitle}>{t('nutrition_facts')}</Text>
          
          {/* Serving Info */}
          {nutritionInfo.servingSize && (
            <View style={styles.servingInfo}>
              <Text style={styles.servingText}>
                {t('serving_size')}: {nutritionInfo.servingSize}
                {nutritionInfo.servingUnit && ` (${nutritionInfo.servingUnit})`}
              </Text>
              {nutritionInfo.servingsPerContainer && (
                <Text style={styles.servingText}>
                  {t('servings_per_container')}: {nutritionInfo.servingsPerContainer}
                </Text>
              )}
            </View>
          )}

          {/* Calories */}
          {nutritionInfo.calories !== null && nutritionInfo.calories !== undefined && (
            <View style={styles.caloriesRow}>
              <Text style={styles.caloriesLabel}>{t('calories')}</Text>
              <Text style={styles.caloriesValue}>{nutritionInfo.calories}</Text>
            </View>
          )}

          <View style={styles.divider} />

          {/* Macronutrients */}
          <View style={styles.nutrientsList}>
            {renderNutrient(t('total_fat'), nutritionInfo.totalFat, 'g')}
            {renderNutrient(t('saturated_fat'), nutritionInfo.saturatedFat, 'g', true)}
            {renderNutrient(t('trans_fat'), nutritionInfo.transFat, 'g', true)}
            {renderNutrient(t('cholesterol'), nutritionInfo.cholesterol, 'mg')}
            {renderNutrient(t('sodium'), nutritionInfo.sodium, 'mg')}
            {renderNutrient(t('total_carbohydrates'), nutritionInfo.totalCarbohydrates, 'g')}
            {renderNutrient(t('dietary_fiber'), nutritionInfo.dietaryFiber, 'g', true)}
            {renderNutrient(t('total_sugars'), nutritionInfo.totalSugars, 'g', true)}
            {renderNutrient(t('added_sugars'), nutritionInfo.addedSugars, 'g', true)}
            {renderNutrient(t('protein'), nutritionInfo.protein, 'g')}
          </View>

          <View style={styles.divider} />

          {/* Vitamins & Minerals */}
          <View style={styles.nutrientsList}>
            {renderNutrient(t('vitamin_d'), nutritionInfo.vitaminD, 'mcg')}
            {renderNutrient(t('calcium'), nutritionInfo.calcium, 'mg')}
            {renderNutrient(t('iron'), nutritionInfo.iron, 'mg')}
            {renderNutrient(t('potassium'), nutritionInfo.potassium, 'mg')}
          </View>
        </View>
      )}

      {/* Ingredients */}
      {ingredients.length > 0 && (
        <View style={styles.ingredientsSection}>
          <Text style={styles.sectionTitle}>{t('ingredients')}</Text>
          <View style={styles.ingredientsList}>
            {ingredients.map((item, index) => {
              const ingredient = item.ingredient;
              const isAllergen = ingredient.isAllergen;
              
              return (
                <View key={item.id} style={styles.ingredientItem}>
                  <Text style={[
                    styles.ingredientText,
                    isAllergen && styles.allergenText
                  ]}>
                    {ingredient.name}
                    {isAllergen && ' ⚠️'}
                    {index < ingredients.length - 1 && ','}
                  </Text>
                </View>
              );
            })}
          </View>

          {/* Dietary Flags */}
          <View style={styles.dietaryFlags}>
            {ingredients.every(i => i.ingredient.isVegan) && (
              <View style={styles.dietaryBadge}>
                <Text style={styles.badgeText}>🌱 {t('vegan')}</Text>
              </View>
            )}
            {ingredients.every(i => i.ingredient.isVegetarian) && (
              <View style={styles.dietaryBadge}>
                <Text style={styles.badgeText}>🥬 {t('vegetarian')}</Text>
              </View>
            )}
            {ingredients.every(i => i.ingredient.isGlutenFree) && (
              <View style={styles.dietaryBadge}>
                <Text style={styles.badgeText}>🌾 {t('gluten_free')}</Text>
              </View>
            )}
            {ingredients.every(i => i.ingredient.isDairyFree) && (
              <View style={styles.dietaryBadge}>
                <Text style={styles.badgeText}>🥛 {t('dairy_free')}</Text>
              </View>
            )}
            {ingredients.every(i => i.ingredient.isNutFree) && (
              <View style={styles.dietaryBadge}>
                <Text style={styles.badgeText}>🥜 {t('nut_free')}</Text>
              </View>
            )}
          </View>

          {/* Allergen Warning */}
          {ingredients.some(i => i.ingredient.isAllergen) && (
            <View style={styles.allergenWarning}>
              <Text style={styles.allergenWarningText}>
                ⚠️ {t('contains_allergens')}
              </Text>
            </View>
          )}
        </View>
      )}
    </ScrollView>
  );
};

// Helper function to render nutrient row
const renderNutrient = (
  label: string,
  value: number | null | undefined,
  unit: string,
  isIndented: boolean = false
): React.ReactNode => {
  if (value === null || value === undefined) {
    return null;
  }

  return (
    <View style={[styles.nutrientRow, isIndented && styles.nutrientRowIndented]}>
      <Text style={[styles.nutrientLabel, isIndented && styles.nutrientLabelIndented]}>
        {label}
      </Text>
      <Text style={styles.nutrientValue}>
        {value}{unit}
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
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
    fontSize: 18,
    fontWeight: '600',
    color: '#666',
    marginBottom: 8,
    textAlign: 'center',
  },
  emptySubtext: {
    fontSize: 14,
    color: '#999',
    textAlign: 'center',
  },
  nutritionFacts: {
    backgroundColor: '#FFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    borderWidth: 2,
    borderColor: '#000',
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#000',
    marginBottom: 12,
    borderBottomWidth: 4,
    borderBottomColor: '#000',
    paddingBottom: 4,
  },
  servingInfo: {
    marginBottom: 8,
  },
  servingText: {
    fontSize: 14,
    color: '#333',
    marginBottom: 4,
  },
  caloriesRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
  },
  caloriesLabel: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#000',
  },
  caloriesValue: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#000',
  },
  divider: {
    height: 2,
    backgroundColor: '#000',
    marginVertical: 8,
  },
  nutrientsList: {
    marginTop: 8,
  },
  nutrientRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 4,
    borderBottomWidth: 1,
    borderBottomColor: '#E0E0E0',
  },
  nutrientRowIndented: {
    paddingLeft: 16,
  },
  nutrientLabel: {
    fontSize: 14,
    color: '#333',
    fontWeight: '600',
  },
  nutrientLabelIndented: {
    fontWeight: 'normal',
  },
  nutrientValue: {
    fontSize: 14,
    color: '#333',
    fontWeight: '600',
  },
  ingredientsSection: {
    backgroundColor: '#FFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
  },
  ingredientsList: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 12,
  },
  ingredientItem: {
    marginRight: 4,
  },
  ingredientText: {
    fontSize: 14,
    color: '#333',
  },
  allergenText: {
    color: '#F44336',
    fontWeight: '600',
  },
  dietaryFlags: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: 12,
    gap: 8,
  },
  dietaryBadge: {
    backgroundColor: '#E8F5E9',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#4CAF50',
  },
  badgeText: {
    fontSize: 12,
    color: '#2E7D32',
    fontWeight: '600',
  },
  allergenWarning: {
    backgroundColor: '#FFF3CD',
    borderLeftWidth: 4,
    borderLeftColor: '#F44336',
    padding: 12,
    borderRadius: 8,
    marginTop: 12,
  },
  allergenWarningText: {
    fontSize: 14,
    color: '#856404',
    fontWeight: '600',
  },
});

export default NutritionFactsDisplay;
