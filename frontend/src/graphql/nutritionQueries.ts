import { gql } from '@apollo/client';

/**
 * GraphQL Queries and Mutations for Nutrition & Allergen Tracking
 * Minimal UI Implementation - Essential operations only
 */

// ============================================================================
// QUERIES
// ============================================================================

/**
 * Get user's allergens for checking against items
 */
export const GET_USER_ALLERGENS = gql`
  query GetUserAllergens {
    getUserAllergens {
      id
      allergenType
      customAllergenName
      severity
      notes
      createdAt
    }
  }
`;

/**
 * Get nutrition info for a specific item
 */
export const GET_ITEM_NUTRITION = gql`
  query GetItemNutrition($itemId: ID!) {
    getItemNutrition(itemId: $itemId) {
      id
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
      createdAt
    }
  }
`;

/**
 * Get ingredients for a specific item
 */
export const GET_ITEM_INGREDIENTS = gql`
  query GetItemIngredients($itemId: ID!) {
    getItemIngredients(itemId: $itemId) {
      id
      ingredient {
        id
        name
        isAllergen
        allergenType
        isVegan
        isVegetarian
        isGlutenFree
        isDairyFree
        isNutFree
      }
      position
      percentage
    }
  }
`;

/**
 * Get pending allergen alerts for user
 */
export const GET_PENDING_ALERTS = gql`
  query GetPendingAlerts {
    getPendingAlerts {
      id
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
`;

// ============================================================================
// MUTATIONS
// ============================================================================

/**
 * Add a new allergen to user's profile
 */
export const ADD_USER_ALLERGEN = gql`
  mutation AddUserAllergen($input: UserAllergenInput!) {
    addUserAllergen(input: $input) {
      id
      allergenType
      customAllergenName
      severity
      notes
      createdAt
    }
  }
`;

/**
 * Remove an allergen from user's profile
 */
export const REMOVE_USER_ALLERGEN = gql`
  mutation RemoveUserAllergen($id: ID!) {
    removeUserAllergen(id: $id)
  }
`;

/**
 * Scan nutrition label and parse data
 * This is the main mutation for the nutrition scanning feature
 */
export const SCAN_NUTRITION_LABEL = gql`
  mutation ScanNutritionLabel($itemId: ID!, $ocrText: String!) {
    scanNutritionLabel(itemId: $itemId, ocrText: $ocrText) {
      success
      message
      nutritionInfo {
        id
        calories
        protein
        totalCarbohydrates
        totalFat
        servingSize
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
        allergenName
        severity
        status
      }
      dietaryViolations
    }
  }
`;

/**
 * Acknowledge an allergen alert (user saw the warning)
 */
export const ACKNOWLEDGE_ALLERGEN_ALERT = gql`
  mutation AcknowledgeAllergenAlert($alertId: ID!, $action: UserAction!) {
    acknowledgeAllergenAlert(alertId: $alertId, action: $action) {
      id
      status
      userAction
      userActionAt
    }
  }
`;

/**
 * Dismiss an alert (user doesn't want to see it again)
 */
export const DISMISS_ALERT = gql`
  mutation DismissAlert($alertId: ID!) {
    dismissAlert(alertId: $alertId)
  }
`;

/**
 * Manually add nutrition info to an item
 */
export const ADD_NUTRITION_INFO = gql`
  mutation AddNutritionInfo($input: NutritionInfoInput!) {
    addNutritionInfo(input: $input) {
      id
      calories
      protein
      totalCarbohydrates
      totalFat
      servingSize
      servingUnit
    }
  }
`;

// ============================================================================
// TYPES (for TypeScript)
// ============================================================================

export interface UserAllergen {
  id: string;
  allergenType: AllergenType;
  customAllergenName?: string;
  severity: Severity;
  notes?: string;
  createdAt: string;
}

export interface NutritionInfo {
  id: string;
  servingSize?: string;
  servingUnit?: string;
  servingsPerContainer?: number;
  calories?: number;
  caloriesFromFat?: number;
  totalFat?: number;
  saturatedFat?: number;
  transFat?: number;
  cholesterol?: number;
  sodium?: number;
  totalCarbohydrates?: number;
  dietaryFiber?: number;
  totalSugars?: number;
  addedSugars?: number;
  protein?: number;
  vitaminD?: number;
  calcium?: number;
  iron?: number;
  potassium?: number;
  createdAt: string;
}

export interface Ingredient {
  id: string;
  name: string;
  isAllergen: boolean;
  allergenType?: AllergenType;
  isVegan: boolean;
  isVegetarian: boolean;
  isGlutenFree: boolean;
  isDairyFree: boolean;
  isNutFree: boolean;
}

export interface ItemIngredient {
  id: string;
  ingredient: Ingredient;
  position?: number;
  percentage?: number;
}

export interface AllergenAlert {
  id: string;
  item?: {
    id: string;
    name: string;
  };
  allergenType: AllergenType;
  allergenName?: string;
  severity?: Severity;
  status: AlertStatus;
  createdAt: string;
}

export interface NutritionScanResult {
  success: boolean;
  message?: string;
  nutritionInfo?: NutritionInfo;
  ingredients: Ingredient[];
  allergenAlerts: AllergenAlert[];
  dietaryViolations: string[];
}

// Enums
export enum AllergenType {
  MILK = 'MILK',
  EGGS = 'EGGS',
  FISH = 'FISH',
  SHELLFISH = 'SHELLFISH',
  TREE_NUTS = 'TREE_NUTS',
  PEANUTS = 'PEANUTS',
  WHEAT = 'WHEAT',
  SOYBEANS = 'SOYBEANS',
  SESAME = 'SESAME',
  GLUTEN = 'GLUTEN',
  CORN = 'CORN',
  SOY = 'SOY',
  CUSTOM = 'CUSTOM',
  DIETARY_RESTRICTION = 'DIETARY_RESTRICTION',
}

export enum Severity {
  MILD = 'MILD',
  MODERATE = 'MODERATE',
  SEVERE = 'SEVERE',
  LIFE_THREATENING = 'LIFE_THREATENING',
}

export enum AlertStatus {
  PENDING = 'PENDING',
  ACKNOWLEDGED = 'ACKNOWLEDGED',
  IGNORED = 'IGNORED',
  ITEM_REMOVED = 'ITEM_REMOVED',
}

export enum UserAction {
  PROCEEDED = 'PROCEEDED',
  CANCELLED = 'CANCELLED',
  REMOVED_ITEM = 'REMOVED_ITEM',
}

// Helper function to get allergen display name
export const getAllergenDisplayName = (type: AllergenType, customName?: string): string => {
  if (type === AllergenType.CUSTOM && customName) {
    return customName;
  }
  
  const names: Record<AllergenType, string> = {
    [AllergenType.MILK]: 'Milk/Dairy',
    [AllergenType.EGGS]: 'Eggs',
    [AllergenType.FISH]: 'Fish',
    [AllergenType.SHELLFISH]: 'Shellfish',
    [AllergenType.TREE_NUTS]: 'Tree Nuts',
    [AllergenType.PEANUTS]: 'Peanuts',
    [AllergenType.WHEAT]: 'Wheat',
    [AllergenType.SOYBEANS]: 'Soybeans',
    [AllergenType.SESAME]: 'Sesame',
    [AllergenType.GLUTEN]: 'Gluten',
    [AllergenType.CORN]: 'Corn',
    [AllergenType.SOY]: 'Soy',
    [AllergenType.CUSTOM]: 'Custom Allergen',
    [AllergenType.DIETARY_RESTRICTION]: 'Dietary Restriction',
  };
  
  return names[type] || type;
};

// Helper function to get severity color
export const getSeverityColor = (severity: Severity): string => {
  const colors: Record<Severity, string> = {
    [Severity.MILD]: '#4CAF50', // Green
    [Severity.MODERATE]: '#FFC107', // Yellow
    [Severity.SEVERE]: '#FF9800', // Orange
    [Severity.LIFE_THREATENING]: '#F44336', // Red
  };
  
  return colors[severity] || '#757575';
};

// Helper function to get severity icon
export const getSeverityIcon = (severity: Severity): string => {
  const icons: Record<Severity, string> = {
    [Severity.MILD]: 'info',
    [Severity.MODERATE]: 'warning',
    [Severity.SEVERE]: 'error',
    [Severity.LIFE_THREATENING]: 'dangerous',
  };
  
  return icons[severity] || 'info';
};
