import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
  ActivityIndicator,
  RefreshControl,
  ScrollView,
} from 'react-native';
import { useQuery, gql } from '@apollo/client';
import { useNavigation, useRoute } from '@react-navigation/native';

const SUGGEST_RECIPES = gql`
  query SuggestRecipesForExpiringItems($householdId: ID!, $daysAhead: Int) {
    suggestRecipesForExpiringItems(householdId: $householdId, daysAhead: $daysAhead) {
      score
      matchPercentage
      urgentItemsUsed
      matchedIngredients
      missingIngredients
      recipe {
        id
        name
        description
        prepTimeMinutes
        cookTimeMinutes
        servings
        difficultyLevel
        cuisineType
        mealType
        ingredients
        caloriesPerServing
        proteinGrams
        carbsGrams
        fatGrams
        imageUrl
        ratingAverage
        ratingCount
        viewCount
        tags
      }
    }
  }
`;

interface RecipeSuggestion {
  score: number;
  matchPercentage: number;
  urgentItemsUsed: number;
  matchedIngredients: string[];
  missingIngredients: string[];
  recipe: {
    id: string;
    name: string;
    description: string;
    prepTimeMinutes: number;
    cookTimeMinutes: number;
    servings: number;
    difficultyLevel: string;
    cuisineType: string;
    mealType: string;
    ingredients: string[];
    caloriesPerServing: number;
    proteinGrams: number;
    carbsGrams: number;
    fatGrams: number;
    imageUrl: string;
    ratingAverage: number;
    ratingCount: number;
    viewCount: number;
    tags: string[];
  };
}

export default function RecipeSuggestionsScreen() {
  const navigation = useNavigation();
  const route = useRoute();
  const { householdId } = route.params as { householdId: string };
  
  const [daysAhead, setDaysAhead] = useState(7);
  
  const { data, loading, error, refetch } = useQuery(SUGGEST_RECIPES, {
    variables: { householdId, daysAhead },
    fetchPolicy: 'network-only',
  });
  
  const suggestions: RecipeSuggestion[] = data?.suggestRecipesForExpiringItems || [];
  
  const renderDifficultyBadge = (level: string) => {
    const colors = {
      EASY: '#4CAF50',
      MEDIUM: '#FF9800',
      HARD: '#F44336',
    };
    
    return (
      <View style={[styles.badge, { backgroundColor: colors[level] || '#999' }]}>
        <Text style={styles.badgeText}>{level}</Text>
      </View>
    );
  };
  
  const renderMatchBadge = (percentage: number) => {
    let color = '#4CAF50';
    if (percentage < 50) color = '#F44336';
    else if (percentage < 75) color = '#FF9800';
    
    return (
      <View style={[styles.matchBadge, { backgroundColor: color }]}>
        <Text style={styles.matchBadgeText}>{percentage.toFixed(0)}% Match</Text>
      </View>
    );
  };
  
  const renderRecipeCard = ({ item }: { item: RecipeSuggestion }) => {
    const { recipe, matchPercentage, urgentItemsUsed, matchedIngredients, missingIngredients } = item;
    const totalTime = (recipe.prepTimeMinutes || 0) + (recipe.cookTimeMinutes || 0);
    
    return (
      <TouchableOpacity
        style={styles.recipeCard}
        onPress={() => navigation.navigate('RecipeDetail', { recipeId: recipe.id })}
      >
        {recipe.imageUrl ? (
          <Image source={{ uri: recipe.imageUrl }} style={styles.recipeImage} />
        ) : (
          <View style={[styles.recipeImage, styles.placeholderImage]}>
            <Text style={styles.placeholderText}>🍳</Text>
          </View>
        )}
        
        <View style={styles.recipeContent}>
          <View style={styles.recipeHeader}>
            <Text style={styles.recipeName} numberOfLines={2}>
              {recipe.name}
            </Text>
            {renderMatchBadge(matchPercentage)}
          </View>
          
          {recipe.description && (
            <Text style={styles.recipeDescription} numberOfLines={2}>
              {recipe.description}
            </Text>
          )}
          
          <View style={styles.recipeMetaRow}>
            <View style={styles.metaItem}>
              <Text style={styles.metaIcon}>⏱️</Text>
              <Text style={styles.metaText}>{totalTime} min</Text>
            </View>
            
            <View style={styles.metaItem}>
              <Text style={styles.metaIcon}>🔥</Text>
              <Text style={styles.metaText}>{recipe.caloriesPerServing || '?'} cal</Text>
            </View>
            
            <View style={styles.metaItem}>
              <Text style={styles.metaIcon}>⭐</Text>
              <Text style={styles.metaText}>
                {recipe.ratingAverage?.toFixed(1) || 'N/A'} ({recipe.ratingCount || 0})
              </Text>
            </View>
            
            {renderDifficultyBadge(recipe.difficultyLevel)}
          </View>
          
          {urgentItemsUsed > 0 && (
            <View style={styles.urgentBanner}>
              <Text style={styles.urgentText}>
                🚨 Uses {urgentItemsUsed} urgent item{urgentItemsUsed > 1 ? 's' : ''}!
              </Text>
            </View>
          )}
          
          <View style={styles.ingredientsSection}>
            <Text style={styles.sectionTitle}>
              ✅ You have ({matchedIngredients.length}):
            </Text>
            <Text style={styles.ingredientsList} numberOfLines={2}>
              {matchedIngredients.slice(0, 3).join(', ')}
              {matchedIngredients.length > 3 && ` +${matchedIngredients.length - 3} more`}
            </Text>
            
            {missingIngredients.length > 0 && (
              <>
                <Text style={styles.sectionTitle}>
                  🛒 You need ({missingIngredients.length}):
                </Text>
                <Text style={styles.ingredientsList} numberOfLines={2}>
                  {missingIngredients.slice(0, 3).join(', ')}
                  {missingIngredients.length > 3 && ` +${missingIngredients.length - 3} more`}
                </Text>
              </>
            )}
          </View>
          
          {recipe.tags && recipe.tags.length > 0 && (
            <View style={styles.tagsRow}>
              {recipe.tags.slice(0, 3).map((tag, index) => (
                <View key={index} style={styles.tag}>
                  <Text style={styles.tagText}>{tag}</Text>
                </View>
              ))}
            </View>
          )}
        </View>
      </TouchableOpacity>
    );
  };
  
  const renderFilterButtons = () => (
    <View style={styles.filterContainer}>
      <Text style={styles.filterLabel}>Show recipes for items expiring in:</Text>
      <View style={styles.filterButtons}>
        {[3, 7, 14].map((days) => (
          <TouchableOpacity
            key={days}
            style={[styles.filterButton, daysAhead === days && styles.filterButtonActive]}
            onPress={() => setDaysAhead(days)}
          >
            <Text
              style={[
                styles.filterButtonText,
                daysAhead === days && styles.filterButtonTextActive,
              ]}
            >
              {days} days
            </Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
  
  if (loading && !data) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#4CAF50" />
        <Text style={styles.loadingText}>Finding perfect recipes...</Text>
      </View>
    );
  }
  
  if (error) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>❌ Error loading recipes</Text>
        <Text style={styles.errorDetail}>{error.message}</Text>
        <TouchableOpacity style={styles.retryButton} onPress={() => refetch()}>
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }
  
  return (
    <View style={styles.container}>
      {renderFilterButtons()}
      
      {suggestions.length === 0 ? (
        <ScrollView
          contentContainerStyle={styles.emptyContainer}
          refreshControl={<RefreshControl refreshing={loading} onRefresh={refetch} />}
        >
          <Text style={styles.emptyIcon}>🍽️</Text>
          <Text style={styles.emptyTitle}>No Recipe Suggestions</Text>
          <Text style={styles.emptyText}>
            We couldn't find recipes matching your expiring items. Try adjusting the time range or add more items to your inventory.
          </Text>
        </ScrollView>
      ) : (
        <FlatList
          data={suggestions}
          renderItem={renderRecipeCard}
          keyExtractor={(item) => item.recipe.id}
          contentContainerStyle={styles.listContainer}
          refreshControl={<RefreshControl refreshing={loading} onRefresh={refetch} />}
          ListHeaderComponent={() => (
            <View style={styles.headerBanner}>
              <Text style={styles.headerBannerText}>
                🎯 Found {suggestions.length} recipe{suggestions.length > 1 ? 's' : ''} to help reduce food waste!
              </Text>
            </View>
          )}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#F5F5F5',
  },
  loadingText: {
    marginTop: 16,
    fontSize: 16,
    color: '#666',
  },
  errorText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#F44336',
    marginBottom: 8,
  },
  errorDetail: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    marginBottom: 16,
  },
  retryButton: {
    backgroundColor: '#4CAF50',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
  },
  retryButtonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },
  filterContainer: {
    backgroundColor: '#FFF',
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#E0E0E0',
  },
  filterLabel: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  filterButtons: {
    flexDirection: 'row',
    gap: 8,
  },
  filterButton: {
    flex: 1,
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#F5F5F5',
    alignItems: 'center',
  },
  filterButtonActive: {
    backgroundColor: '#4CAF50',
  },
  filterButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#666',
  },
  filterButtonTextActive: {
    color: '#FFF',
  },
  headerBanner: {
    backgroundColor: '#E8F5E9',
    padding: 16,
    marginBottom: 8,
    borderRadius: 8,
  },
  headerBannerText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2E7D32',
    textAlign: 'center',
  },
  listContainer: {
    padding: 16,
  },
  recipeCard: {
    backgroundColor: '#FFF',
    borderRadius: 12,
    marginBottom: 16,
    overflow: 'hidden',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  recipeImage: {
    width: '100%',
    height: 200,
    backgroundColor: '#E0E0E0',
  },
  placeholderImage: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  placeholderText: {
    fontSize: 64,
  },
  recipeContent: {
    padding: 16,
  },
  recipeHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 8,
  },
  recipeName: {
    flex: 1,
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginRight: 8,
  },
  matchBadge: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
  },
  matchBadgeText: {
    color: '#FFF',
    fontSize: 12,
    fontWeight: 'bold',
  },
  recipeDescription: {
    fontSize: 14,
    color: '#666',
    marginBottom: 12,
    lineHeight: 20,
  },
  recipeMetaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
    gap: 12,
  },
  metaItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  metaIcon: {
    fontSize: 14,
  },
  metaText: {
    fontSize: 12,
    color: '#666',
  },
  badge: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
  },
  badgeText: {
    color: '#FFF',
    fontSize: 10,
    fontWeight: 'bold',
  },
  urgentBanner: {
    backgroundColor: '#FFEBEE',
    padding: 8,
    borderRadius: 8,
    marginBottom: 12,
  },
  urgentText: {
    color: '#C62828',
    fontSize: 13,
    fontWeight: '600',
    textAlign: 'center',
  },
  ingredientsSection: {
    marginBottom: 12,
  },
  sectionTitle: {
    fontSize: 13,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
    marginTop: 8,
  },
  ingredientsList: {
    fontSize: 12,
    color: '#666',
    lineHeight: 18,
  },
  tagsRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 6,
  },
  tag: {
    backgroundColor: '#E3F2FD',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
  },
  tagText: {
    fontSize: 11,
    color: '#1976D2',
    fontWeight: '500',
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
  },
  emptyIcon: {
    fontSize: 64,
    marginBottom: 16,
  },
  emptyTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  emptyText: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    lineHeight: 20,
  },
});
