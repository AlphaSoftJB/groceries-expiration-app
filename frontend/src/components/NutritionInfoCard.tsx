import React from 'react';
import { View, Text, StyleSheet, ScrollView, ActivityIndicator } from 'react-native';
import { useQuery } from '@apollo/client';
import { GET_ITEM_NUTRITION, GET_ITEM_INGREDIENTS, NutritionInfo, ItemIngredient } from '../graphql/nutritionQueries';

interface NutritionInfoCardProps {
  itemId: string;
}

export const NutritionInfoCard: React.FC<NutritionInfoCardProps> = ({ itemId }) => {
  const { data: nutritionData, loading: nutritionLoading } = useQuery(GET_ITEM_NUTRITION, {
    variables: { itemId },
    skip: !itemId,
  });

  const { data: ingredientsData, loading: ingredientsLoading } = useQuery(GET_ITEM_INGREDIENTS, {
    variables: { itemId },
    skip: !itemId,
  });

  if (nutritionLoading || ingredientsLoading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#4CAF50" />
        <Text style={styles.loadingText}>Loading nutrition info...</Text>
      </View>
    );
  }

  const nutrition: NutritionInfo | null = nutritionData?.getItemNutrition;
  const ingredients: ItemIngredient[] = ingredientsData?.getItemIngredients || [];

  if (!nutrition) {
    return (
      <View style={styles.noDataContainer}>
        <Text style={styles.noDataText}>No nutrition information available</Text>
        <Text style={styles.noDataSubtext}>Scan the nutrition label to add details</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      {/* Nutrition Facts Label */}
      <View style={styles.nutritionLabel}>
        <Text style={styles.title}>Nutrition Facts</Text>
        {nutrition.servingSize && (
          <Text style={styles.servingSize}>
            Serving Size: {nutrition.servingSize} {nutrition.servingUnit || ''}
          </Text>
        )}
        {nutrition.servingsPerContainer && (
          <Text style={styles.servingsPerContainer}>
            Servings Per Container: {nutrition.servingsPerContainer}
          </Text>
        )}

        <View style={styles.dividerThick} />

        {/* Calories */}
        {nutrition.calories !== null && nutrition.calories !== undefined && (
          <View style={styles.caloriesRow}>
            <Text style={styles.caloriesLabel}>Calories</Text>
            <Text style={styles.caloriesValue}>{nutrition.calories}</Text>
          </View>
        )}

        <View style={styles.dividerMedium} />
        <Text style={styles.dvHeader}>% Daily Value*</Text>

        {/* Macronutrients */}
        {nutrition.totalFat !== null && nutrition.totalFat !== undefined && (
          <NutrientRow
            label="Total Fat"
            value={`${nutrition.totalFat}g`}
            dailyValue={calculateDV(nutrition.totalFat, 78)}
            isBold
          />
        )}
        {nutrition.saturatedFat !== null && nutrition.saturatedFat !== undefined && (
          <NutrientRow
            label="Saturated Fat"
            value={`${nutrition.saturatedFat}g`}
            dailyValue={calculateDV(nutrition.saturatedFat, 20)}
            indent
          />
        )}
        {nutrition.transFat !== null && nutrition.transFat !== undefined && (
          <NutrientRow label="Trans Fat" value={`${nutrition.transFat}g`} indent />
        )}

        {nutrition.cholesterol !== null && nutrition.cholesterol !== undefined && (
          <NutrientRow
            label="Cholesterol"
            value={`${nutrition.cholesterol}mg`}
            dailyValue={calculateDV(nutrition.cholesterol, 300)}
            isBold
          />
        )}

        {nutrition.sodium !== null && nutrition.sodium !== undefined && (
          <NutrientRow
            label="Sodium"
            value={`${nutrition.sodium}mg`}
            dailyValue={calculateDV(nutrition.sodium, 2300)}
            isBold
          />
        )}

        {nutrition.totalCarbohydrates !== null && nutrition.totalCarbohydrates !== undefined && (
          <NutrientRow
            label="Total Carbohydrate"
            value={`${nutrition.totalCarbohydrates}g`}
            dailyValue={calculateDV(nutrition.totalCarbohydrates, 275)}
            isBold
          />
        )}
        {nutrition.dietaryFiber !== null && nutrition.dietaryFiber !== undefined && (
          <NutrientRow
            label="Dietary Fiber"
            value={`${nutrition.dietaryFiber}g`}
            dailyValue={calculateDV(nutrition.dietaryFiber, 28)}
            indent
          />
        )}
        {nutrition.totalSugars !== null && nutrition.totalSugars !== undefined && (
          <NutrientRow label="Total Sugars" value={`${nutrition.totalSugars}g`} indent />
        )}
        {nutrition.addedSugars !== null && nutrition.addedSugars !== undefined && (
          <NutrientRow
            label="Added Sugars"
            value={`${nutrition.addedSugars}g`}
            dailyValue={calculateDV(nutrition.addedSugars, 50)}
            indent={2}
          />
        )}

        {nutrition.protein !== null && nutrition.protein !== undefined && (
          <NutrientRow label="Protein" value={`${nutrition.protein}g`} isBold />
        )}

        <View style={styles.dividerMedium} />

        {/* Vitamins & Minerals */}
        {nutrition.vitaminD !== null && nutrition.vitaminD !== undefined && (
          <NutrientRow
            label="Vitamin D"
            value={`${nutrition.vitaminD}mcg`}
            dailyValue={calculateDV(nutrition.vitaminD, 20)}
          />
        )}
        {nutrition.calcium !== null && nutrition.calcium !== undefined && (
          <NutrientRow
            label="Calcium"
            value={`${nutrition.calcium}mg`}
            dailyValue={calculateDV(nutrition.calcium, 1300)}
          />
        )}
        {nutrition.iron !== null && nutrition.iron !== undefined && (
          <NutrientRow
            label="Iron"
            value={`${nutrition.iron}mg`}
            dailyValue={calculateDV(nutrition.iron, 18)}
          />
        )}
        {nutrition.potassium !== null && nutrition.potassium !== undefined && (
          <NutrientRow
            label="Potassium"
            value={`${nutrition.potassium}mg`}
            dailyValue={calculateDV(nutrition.potassium, 4700)}
          />
        )}

        <View style={styles.dividerThin} />
        <Text style={styles.disclaimer}>
          * The % Daily Value (DV) tells you how much a nutrient in a serving of food contributes
          to a daily diet. 2,000 calories a day is used for general nutrition advice.
        </Text>
      </View>

      {/* Ingredients Section */}
      {ingredients.length > 0 && (
        <View style={styles.ingredientsSection}>
          <Text style={styles.ingredientsTitle}>Ingredients</Text>
          <View style={styles.ingredientsList}>
            {ingredients.map((item, index) => (
              <View key={item.id} style={styles.ingredientItem}>
                <Text style={styles.ingredientText}>
                  {item.ingredient.name}
                  {index < ingredients.length - 1 ? ', ' : ''}
                </Text>
                {item.ingredient.isAllergen && (
                  <View style={styles.allergenBadge}>
                    <Text style={styles.allergenBadgeText}>⚠️ Allergen</Text>
                  </View>
                )}
              </View>
            ))}
          </View>
        </View>
      )}
    </ScrollView>
  );
};

// Helper component for nutrient rows
interface NutrientRowProps {
  label: string;
  value: string;
  dailyValue?: number;
  isBold?: boolean;
  indent?: number | boolean;
}

const NutrientRow: React.FC<NutrientRowProps> = ({
  label,
  value,
  dailyValue,
  isBold,
  indent,
}) => {
  const indentValue = typeof indent === 'number' ? indent * 16 : indent ? 16 : 0;

  return (
    <View style={[styles.nutrientRow, { paddingLeft: indentValue }]}>
      <Text style={[styles.nutrientLabel, isBold && styles.boldText]}>
        {label} <Text style={styles.nutrientValue}>{value}</Text>
      </Text>
      {dailyValue !== undefined && (
        <Text style={[styles.nutrientDV, isBold && styles.boldText]}>{dailyValue}%</Text>
      )}
    </View>
  );
};

// Helper function to calculate % Daily Value
const calculateDV = (amount: number, dailyValue: number): number => {
  return Math.round((amount / dailyValue) * 100);
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  loadingContainer: {
    padding: 20,
    alignItems: 'center',
    justifyContent: 'center',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 14,
    color: '#666',
  },
  noDataContainer: {
    padding: 20,
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
    borderRadius: 8,
    margin: 10,
  },
  noDataText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 5,
  },
  noDataSubtext: {
    fontSize: 14,
    color: '#666',
  },
  nutritionLabel: {
    backgroundColor: '#fff',
    borderWidth: 2,
    borderColor: '#000',
    borderRadius: 4,
    padding: 12,
    margin: 10,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  servingSize: {
    fontSize: 14,
    marginBottom: 2,
  },
  servingsPerContainer: {
    fontSize: 12,
    color: '#666',
    marginBottom: 8,
  },
  dividerThick: {
    height: 10,
    backgroundColor: '#000',
    marginVertical: 4,
  },
  dividerMedium: {
    height: 5,
    backgroundColor: '#000',
    marginVertical: 4,
  },
  dividerThin: {
    height: 1,
    backgroundColor: '#000',
    marginVertical: 8,
  },
  caloriesRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 4,
  },
  caloriesLabel: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  caloriesValue: {
    fontSize: 32,
    fontWeight: 'bold',
  },
  dvHeader: {
    fontSize: 10,
    textAlign: 'right',
    fontWeight: '600',
    marginBottom: 4,
  },
  nutrientRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 2,
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  nutrientLabel: {
    fontSize: 14,
    flex: 1,
  },
  nutrientValue: {
    fontWeight: 'normal',
  },
  nutrientDV: {
    fontSize: 14,
    fontWeight: '600',
  },
  boldText: {
    fontWeight: 'bold',
  },
  disclaimer: {
    fontSize: 10,
    color: '#666',
    marginTop: 8,
    lineHeight: 14,
  },
  ingredientsSection: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 16,
    margin: 10,
    marginTop: 0,
  },
  ingredientsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  ingredientsList: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  ingredientItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 4,
  },
  ingredientText: {
    fontSize: 14,
    color: '#333',
  },
  allergenBadge: {
    backgroundColor: '#FF5252',
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
    marginLeft: 6,
  },
  allergenBadgeText: {
    color: '#fff',
    fontSize: 10,
    fontWeight: '600',
  },
});
