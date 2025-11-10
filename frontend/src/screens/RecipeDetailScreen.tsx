import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { useQuery, useMutation, gql } from '@apollo/client';
import { useRoute } from '@react-navigation/native';

const GET_RECIPE = gql`
  query GetRecipeById($id: ID!) {
    getRecipeById(id: $id) {
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
      instructions
      caloriesPerServing
      proteinGrams
      carbsGrams
      fatGrams
      imageUrl
      videoUrl
      source
      sourceUrl
      ratingAverage
      ratingCount
      viewCount
      tags
    }
  }
`;

const RATE_RECIPE = gql`
  mutation RateRecipe($recipeId: ID!, $rating: Float!) {
    rateRecipe(recipeId: $recipeId, rating: $rating) {
      id
      ratingAverage
      ratingCount
    }
  }
`;

const VIEW_RECIPE = gql`
  mutation ViewRecipe($recipeId: ID!) {
    viewRecipe(recipeId: $recipeId) {
      id
      viewCount
    }
  }
`;

export default function RecipeDetailScreen() {
  const route = useRoute();
  const { recipeId } = route.params as { recipeId: string };
  
  const [selectedRating, setSelectedRating] = useState(0);
  
  const { data, loading, error } = useQuery(GET_RECIPE, {
    variables: { id: recipeId },
    onCompleted: () => {
      viewRecipeMutation({ variables: { recipeId } });
    },
  });
  
  const [rateRecipeMutation] = useMutation(RATE_RECIPE, {
    refetchQueries: [{ query: GET_RECIPE, variables: { id: recipeId } }],
  });
  
  const [viewRecipeMutation] = useMutation(VIEW_RECIPE);
  
  const recipe = data?.getRecipeById;
  
  const handleRating = (rating: number) => {
    setSelectedRating(rating);
    rateRecipeMutation({ variables: { recipeId, rating } });
  };
  
  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#4CAF50" />
      </View>
    );
  }
  
  if (error || !recipe) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>❌ Recipe not found</Text>
      </View>
    );
  }
  
  const totalTime = (recipe.prepTimeMinutes || 0) + (recipe.cookTimeMinutes || 0);
  
  return (
    <ScrollView style={styles.container}>
      {recipe.imageUrl ? (
        <Image source={{ uri: recipe.imageUrl }} style={styles.heroImage} />
      ) : (
        <View style={[styles.heroImage, styles.placeholderHero]}>
          <Text style={styles.placeholderIcon}>🍳</Text>
        </View>
      )}
      
      <View style={styles.content}>
        <Text style={styles.recipeName}>{recipe.name}</Text>
        
        {recipe.description && (
          <Text style={styles.description}>{recipe.description}</Text>
        )}
        
        {/* Meta Info */}
        <View style={styles.metaContainer}>
          <View style={styles.metaCard}>
            <Text style={styles.metaIcon}>⏱️</Text>
            <Text style={styles.metaLabel}>Total Time</Text>
            <Text style={styles.metaValue}>{totalTime} min</Text>
          </View>
          
          <View style={styles.metaCard}>
            <Text style={styles.metaIcon}>🍽️</Text>
            <Text style={styles.metaLabel}>Servings</Text>
            <Text style={styles.metaValue}>{recipe.servings || 'N/A'}</Text>
          </View>
          
          <View style={styles.metaCard}>
            <Text style={styles.metaIcon}>📊</Text>
            <Text style={styles.metaLabel}>Difficulty</Text>
            <Text style={styles.metaValue}>{recipe.difficultyLevel}</Text>
          </View>
          
          <View style={styles.metaCard}>
            <Text style={styles.metaIcon}>🔥</Text>
            <Text style={styles.metaLabel}>Calories</Text>
            <Text style={styles.metaValue}>{recipe.caloriesPerServing || 'N/A'}</Text>
          </View>
        </View>
        
        {/* Nutrition Info */}
        {(recipe.proteinGrams || recipe.carbsGrams || recipe.fatGrams) && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Nutrition per Serving</Text>
            <View style={styles.nutritionRow}>
              {recipe.proteinGrams && (
                <View style={styles.nutritionItem}>
                  <Text style={styles.nutritionLabel}>Protein</Text>
                  <Text style={styles.nutritionValue}>{recipe.proteinGrams}g</Text>
                </View>
              )}
              {recipe.carbsGrams && (
                <View style={styles.nutritionItem}>
                  <Text style={styles.nutritionLabel}>Carbs</Text>
                  <Text style={styles.nutritionValue}>{recipe.carbsGrams}g</Text>
                </View>
              )}
              {recipe.fatGrams && (
                <View style={styles.nutritionItem}>
                  <Text style={styles.nutritionLabel}>Fat</Text>
                  <Text style={styles.nutritionValue}>{recipe.fatGrams}g</Text>
                </View>
              )}
            </View>
          </View>
        )}
        
        {/* Ingredients */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Ingredients</Text>
          {recipe.ingredients.map((ingredient: string, index: number) => (
            <View key={index} style={styles.ingredientItem}>
              <Text style={styles.bullet}>•</Text>
              <Text style={styles.ingredientText}>{ingredient}</Text>
            </View>
          ))}
        </View>
        
        {/* Instructions */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Instructions</Text>
          {recipe.instructions.map((instruction: string, index: number) => (
            <View key={index} style={styles.instructionItem}>
              <View style={styles.stepNumber}>
                <Text style={styles.stepNumberText}>{index + 1}</Text>
              </View>
              <Text style={styles.instructionText}>{instruction}</Text>
            </View>
          ))}
        </View>
        
        {/* Tags */}
        {recipe.tags && recipe.tags.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Tags</Text>
            <View style={styles.tagsContainer}>
              {recipe.tags.map((tag: string, index: number) => (
                <View key={index} style={styles.tag}>
                  <Text style={styles.tagText}>{tag}</Text>
                </View>
              ))}
            </View>
          </View>
        )}
        
        {/* Rating */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Rate this Recipe</Text>
          <View style={styles.ratingContainer}>
            <View style={styles.currentRating}>
              <Text style={styles.ratingValue}>
                ⭐ {recipe.ratingAverage?.toFixed(1) || 'N/A'}
              </Text>
              <Text style={styles.ratingCount}>
                ({recipe.ratingCount || 0} ratings)
              </Text>
            </View>
            
            <View style={styles.ratingStars}>
              {[1, 2, 3, 4, 5].map((star) => (
                <TouchableOpacity key={star} onPress={() => handleRating(star)}>
                  <Text style={styles.star}>
                    {selectedRating >= star || recipe.ratingAverage >= star ? '⭐' : '☆'}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>
        </View>
        
        {/* Source */}
        {recipe.source && (
          <View style={styles.section}>
            <Text style={styles.sourceText}>
              Source: {recipe.source}
            </Text>
            {recipe.sourceUrl && (
              <Text style={styles.sourceUrl}>{recipe.sourceUrl}</Text>
            )}
          </View>
        )}
        
        {/* Stats */}
        <View style={styles.statsContainer}>
          <Text style={styles.statsText}>
            👁️ {recipe.viewCount || 0} views
          </Text>
        </View>
      </View>
    </ScrollView>
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
    backgroundColor: '#F5F5F5',
  },
  errorText: {
    fontSize: 18,
    color: '#F44336',
  },
  heroImage: {
    width: '100%',
    height: 300,
    backgroundColor: '#E0E0E0',
  },
  placeholderHero: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  placeholderIcon: {
    fontSize: 80,
  },
  content: {
    padding: 16,
  },
  recipeName: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 12,
  },
  description: {
    fontSize: 16,
    color: '#666',
    lineHeight: 24,
    marginBottom: 20,
  },
  metaContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
    marginBottom: 24,
  },
  metaCard: {
    flex: 1,
    minWidth: '45%',
    backgroundColor: '#FFF',
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  metaIcon: {
    fontSize: 32,
    marginBottom: 8,
  },
  metaLabel: {
    fontSize: 12,
    color: '#999',
    marginBottom: 4,
  },
  metaValue: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  section: {
    backgroundColor: '#FFF',
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 16,
  },
  nutritionRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  nutritionItem: {
    alignItems: 'center',
  },
  nutritionLabel: {
    fontSize: 12,
    color: '#999',
    marginBottom: 4,
  },
  nutritionValue: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#4CAF50',
  },
  ingredientItem: {
    flexDirection: 'row',
    marginBottom: 8,
  },
  bullet: {
    fontSize: 16,
    color: '#4CAF50',
    marginRight: 8,
    fontWeight: 'bold',
  },
  ingredientText: {
    flex: 1,
    fontSize: 15,
    color: '#333',
    lineHeight: 22,
  },
  instructionItem: {
    flexDirection: 'row',
    marginBottom: 16,
  },
  stepNumber: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: '#4CAF50',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  stepNumberText: {
    color: '#FFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  instructionText: {
    flex: 1,
    fontSize: 15,
    color: '#333',
    lineHeight: 22,
  },
  tagsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  tag: {
    backgroundColor: '#E3F2FD',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  tagText: {
    fontSize: 13,
    color: '#1976D2',
    fontWeight: '500',
  },
  ratingContainer: {
    alignItems: 'center',
  },
  currentRating: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
    gap: 8,
  },
  ratingValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  ratingCount: {
    fontSize: 14,
    color: '#999',
  },
  ratingStars: {
    flexDirection: 'row',
    gap: 8,
  },
  star: {
    fontSize: 32,
  },
  sourceText: {
    fontSize: 13,
    color: '#666',
    fontStyle: 'italic',
  },
  sourceUrl: {
    fontSize: 12,
    color: '#1976D2',
    marginTop: 4,
  },
  statsContainer: {
    alignItems: 'center',
    paddingVertical: 16,
  },
  statsText: {
    fontSize: 13,
    color: '#999',
  },
});
