-- Nutrition & Allergen Tracking Schema
-- Version 2.0 - Adds comprehensive nutrition and allergen management

-- ============================================================================
-- NUTRITION INFORMATION TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS nutrition_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    
    -- Serving Information
    serving_size VARCHAR(100),
    serving_unit VARCHAR(50),
    servings_per_container DECIMAL(10,2),
    
    -- Calories
    calories INT,
    calories_from_fat INT,
    
    -- Macronutrients (in grams)
    total_fat DECIMAL(10,2),
    saturated_fat DECIMAL(10,2),
    trans_fat DECIMAL(10,2),
    polyunsaturated_fat DECIMAL(10,2),
    monounsaturated_fat DECIMAL(10,2),
    
    cholesterol DECIMAL(10,2), -- in mg
    sodium DECIMAL(10,2), -- in mg
    
    total_carbohydrates DECIMAL(10,2),
    dietary_fiber DECIMAL(10,2),
    total_sugars DECIMAL(10,2),
    added_sugars DECIMAL(10,2),
    sugar_alcohols DECIMAL(10,2),
    
    protein DECIMAL(10,2),
    
    -- Vitamins (in mg or mcg)
    vitamin_a DECIMAL(10,2),
    vitamin_c DECIMAL(10,2),
    vitamin_d DECIMAL(10,2),
    vitamin_e DECIMAL(10,2),
    vitamin_k DECIMAL(10,2),
    vitamin_b6 DECIMAL(10,2),
    vitamin_b12 DECIMAL(10,2),
    
    -- Minerals (in mg or mcg)
    calcium DECIMAL(10,2),
    iron DECIMAL(10,2),
    potassium DECIMAL(10,2),
    magnesium DECIMAL(10,2),
    zinc DECIMAL(10,2),
    
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    INDEX idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- INGREDIENTS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    
    -- Allergen flags
    is_allergen BOOLEAN DEFAULT FALSE,
    allergen_type ENUM(
        'MILK', 'EGGS', 'FISH', 'SHELLFISH', 'TREE_NUTS', 'PEANUTS', 
        'WHEAT', 'SOYBEANS', 'SESAME', 'GLUTEN', 'CORN', 'SOY'
    ),
    
    -- Dietary flags
    is_vegan BOOLEAN DEFAULT TRUE,
    is_vegetarian BOOLEAN DEFAULT TRUE,
    is_gluten_free BOOLEAN DEFAULT TRUE,
    is_dairy_free BOOLEAN DEFAULT TRUE,
    is_nut_free BOOLEAN DEFAULT TRUE,
    
    -- Common names and aliases (JSON array)
    aliases JSON,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_name (name),
    INDEX idx_allergen (is_allergen, allergen_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- ITEM INGREDIENTS (Many-to-Many)
-- ============================================================================
CREATE TABLE IF NOT EXISTS item_ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    
    -- Order in ingredient list (1 = first ingredient)
    position INT,
    
    -- Percentage if available
    percentage DECIMAL(5,2),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    UNIQUE KEY unique_item_ingredient (item_id, ingredient_id),
    INDEX idx_item_id (item_id),
    INDEX idx_ingredient_id (ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- USER ALLERGENS & DIETARY RESTRICTIONS
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_allergens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    
    -- Allergen type
    allergen_type ENUM(
        'MILK', 'EGGS', 'FISH', 'SHELLFISH', 'TREE_NUTS', 'PEANUTS', 
        'WHEAT', 'SOYBEANS', 'SESAME', 'GLUTEN', 'CORN', 'SOY',
        'CUSTOM'
    ) NOT NULL,
    
    -- Custom allergen name (if allergen_type = 'CUSTOM')
    custom_allergen_name VARCHAR(255),
    
    -- Severity
    severity ENUM('MILD', 'MODERATE', 'SEVERE', 'LIFE_THREATENING') DEFAULT 'MODERATE',
    
    -- Notes
    notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_allergen_type (allergen_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- USER DIETARY PREFERENCES
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_dietary_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    
    -- Dietary restrictions
    is_vegan BOOLEAN DEFAULT FALSE,
    is_vegetarian BOOLEAN DEFAULT FALSE,
    is_gluten_free BOOLEAN DEFAULT FALSE,
    is_dairy_free BOOLEAN DEFAULT FALSE,
    is_nut_free BOOLEAN DEFAULT FALSE,
    is_kosher BOOLEAN DEFAULT FALSE,
    is_halal BOOLEAN DEFAULT FALSE,
    is_low_carb BOOLEAN DEFAULT FALSE,
    is_keto BOOLEAN DEFAULT FALSE,
    is_paleo BOOLEAN DEFAULT FALSE,
    
    -- Nutritional goals (daily targets)
    daily_calorie_goal INT,
    daily_protein_goal DECIMAL(10,2),
    daily_carb_goal DECIMAL(10,2),
    daily_fat_goal DECIMAL(10,2),
    daily_fiber_goal DECIMAL(10,2),
    daily_sugar_limit DECIMAL(10,2),
    daily_sodium_limit DECIMAL(10,2),
    
    -- Preferences
    avoid_ingredients JSON, -- Array of ingredient IDs to avoid
    preferred_ingredients JSON, -- Array of ingredient IDs user prefers
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- ALLERGEN ALERTS LOG
-- ============================================================================
CREATE TABLE IF NOT EXISTS allergen_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    
    -- Alert details
    allergen_type ENUM(
        'MILK', 'EGGS', 'FISH', 'SHELLFISH', 'TREE_NUTS', 'PEANUTS', 
        'WHEAT', 'SOYBEANS', 'SESAME', 'GLUTEN', 'CORN', 'SOY',
        'CUSTOM', 'DIETARY_RESTRICTION'
    ) NOT NULL,
    
    allergen_name VARCHAR(255),
    severity ENUM('MILD', 'MODERATE', 'SEVERE', 'LIFE_THREATENING'),
    
    -- Alert status
    status ENUM('PENDING', 'ACKNOWLEDGED', 'IGNORED', 'ITEM_REMOVED') DEFAULT 'PENDING',
    
    -- User action
    user_action ENUM('PROCEEDED', 'CANCELLED', 'REMOVED_ITEM') DEFAULT NULL,
    user_action_at TIMESTAMP NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_item_id (item_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- CONSUMPTION LOG
-- ============================================================================
CREATE TABLE IF NOT EXISTS consumption_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    
    -- Consumption details
    consumed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    servings_consumed DECIMAL(10,2) DEFAULT 1.0,
    
    -- Nutritional totals (calculated from nutrition_info * servings)
    total_calories INT,
    total_protein DECIMAL(10,2),
    total_carbs DECIMAL(10,2),
    total_fat DECIMAL(10,2),
    total_fiber DECIMAL(10,2),
    total_sugar DECIMAL(10,2),
    total_sodium DECIMAL(10,2),
    
    -- Meal type
    meal_type ENUM('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK', 'OTHER'),
    
    -- Notes
    notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_consumed_at (consumed_at),
    INDEX idx_meal_type (meal_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- DAILY NUTRITION SUMMARY
-- ============================================================================
CREATE TABLE IF NOT EXISTS daily_nutrition_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    
    -- Daily totals
    total_calories INT DEFAULT 0,
    total_protein DECIMAL(10,2) DEFAULT 0,
    total_carbs DECIMAL(10,2) DEFAULT 0,
    total_fat DECIMAL(10,2) DEFAULT 0,
    total_fiber DECIMAL(10,2) DEFAULT 0,
    total_sugar DECIMAL(10,2) DEFAULT 0,
    total_sodium DECIMAL(10,2) DEFAULT 0,
    
    -- Meal breakdown
    breakfast_calories INT DEFAULT 0,
    lunch_calories INT DEFAULT 0,
    dinner_calories INT DEFAULT 0,
    snack_calories INT DEFAULT 0,
    
    -- Goals met
    calorie_goal_met BOOLEAN DEFAULT FALSE,
    protein_goal_met BOOLEAN DEFAULT FALSE,
    carb_goal_met BOOLEAN DEFAULT FALSE,
    fat_goal_met BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, date),
    INDEX idx_user_id (user_id),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- SEED COMMON ALLERGENS AND INGREDIENTS
-- ============================================================================

-- Insert common allergens
INSERT INTO ingredients (name, is_allergen, allergen_type, is_vegan, is_vegetarian, is_gluten_free, is_dairy_free, is_nut_free, aliases) VALUES
('Milk', TRUE, 'MILK', FALSE, TRUE, TRUE, FALSE, TRUE, '["dairy", "lactose", "cream", "butter", "cheese"]'),
('Eggs', TRUE, 'EGGS', FALSE, TRUE, TRUE, TRUE, TRUE, '["egg", "albumin", "egg white", "egg yolk"]'),
('Fish', TRUE, 'FISH', FALSE, FALSE, TRUE, TRUE, TRUE, '["salmon", "tuna", "cod", "halibut"]'),
('Shellfish', TRUE, 'SHELLFISH', FALSE, FALSE, TRUE, TRUE, TRUE, '["shrimp", "crab", "lobster", "clams", "oysters"]'),
('Tree Nuts', TRUE, 'TREE_NUTS', TRUE, TRUE, TRUE, TRUE, FALSE, '["almonds", "walnuts", "cashews", "pecans", "pistachios"]'),
('Peanuts', TRUE, 'PEANUTS', TRUE, TRUE, TRUE, TRUE, FALSE, '["peanut", "groundnut", "peanut butter"]'),
('Wheat', TRUE, 'WHEAT', TRUE, TRUE, FALSE, TRUE, TRUE, '["flour", "wheat flour", "whole wheat", "durum"]'),
('Soybeans', TRUE, 'SOYBEANS', TRUE, TRUE, TRUE, TRUE, TRUE, '["soy", "soya", "tofu", "edamame", "soy sauce"]'),
('Sesame', TRUE, 'SESAME', TRUE, TRUE, TRUE, TRUE, TRUE, '["sesame seeds", "tahini", "sesame oil"]'),
('Gluten', TRUE, 'GLUTEN', TRUE, TRUE, FALSE, TRUE, TRUE, '["wheat gluten", "barley", "rye"]'),
('Corn', TRUE, 'CORN', TRUE, TRUE, TRUE, TRUE, TRUE, '["maize", "corn syrup", "cornstarch"]'),
('Soy', TRUE, 'SOY', TRUE, TRUE, TRUE, TRUE, TRUE, '["soya", "soybean", "soy lecithin"]')
ON DUPLICATE KEY UPDATE name=name;

-- Insert common non-allergen ingredients
INSERT INTO ingredients (name, is_allergen, is_vegan, is_vegetarian, is_gluten_free, is_dairy_free, is_nut_free, aliases) VALUES
('Sugar', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["sucrose", "cane sugar", "white sugar"]'),
('Salt', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["sodium chloride", "sea salt", "table salt"]'),
('Water', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["H2O", "purified water"]'),
('Citric Acid', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["E330"]'),
('Natural Flavors', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["natural flavoring"]'),
('Artificial Flavors', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["artificial flavoring"]'),
('Preservatives', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["preservative"]'),
('Vitamin C', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["ascorbic acid", "E300"]'),
('Vitamin D', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["cholecalciferol", "ergocalciferol"]'),
('Iron', FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, '["ferrous sulfate"]')
ON DUPLICATE KEY UPDATE name=name;

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================

-- Additional indexes for common queries
CREATE INDEX idx_nutrition_calories ON nutrition_info(calories);
CREATE INDEX idx_ingredients_allergen_type ON ingredients(allergen_type);
CREATE INDEX idx_consumption_date ON consumption_log(consumed_at);
CREATE INDEX idx_daily_summary_date ON daily_nutrition_summary(date);

-- ============================================================================
-- VIEWS FOR COMMON QUERIES
-- ============================================================================

-- View: Items with allergen warnings for a user
CREATE OR REPLACE VIEW user_allergen_items AS
SELECT 
    u.id AS user_id,
    i.id AS item_id,
    i.name AS item_name,
    ua.allergen_type,
    ua.severity,
    ing.name AS allergen_name
FROM users u
JOIN user_allergens ua ON u.id = ua.user_id
JOIN item_ingredients ii ON ii.ingredient_id IN (
    SELECT id FROM ingredients 
    WHERE allergen_type = ua.allergen_type
)
JOIN items i ON i.id = ii.item_id
JOIN ingredients ing ON ing.id = ii.ingredient_id;

-- View: Daily nutrition summary with goals
CREATE OR REPLACE VIEW daily_nutrition_with_goals AS
SELECT 
    dns.user_id,
    dns.date,
    dns.total_calories,
    dns.total_protein,
    dns.total_carbs,
    dns.total_fat,
    udp.daily_calorie_goal,
    udp.daily_protein_goal,
    udp.daily_carb_goal,
    udp.daily_fat_goal,
    ROUND((dns.total_calories / NULLIF(udp.daily_calorie_goal, 0)) * 100, 2) AS calorie_goal_percentage,
    ROUND((dns.total_protein / NULLIF(udp.daily_protein_goal, 0)) * 100, 2) AS protein_goal_percentage,
    ROUND((dns.total_carbs / NULLIF(udp.daily_carb_goal, 0)) * 100, 2) AS carb_goal_percentage,
    ROUND((dns.total_fat / NULLIF(udp.daily_fat_goal, 0)) * 100, 2) AS fat_goal_percentage
FROM daily_nutrition_summary dns
LEFT JOIN user_dietary_preferences udp ON dns.user_id = udp.user_id;
