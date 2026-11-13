
-- ============================================
-- TEND DATABASE
-- MySQL 8.0.16+
-- Unified Food Architecture
-- ============================================

CREATE DATABASE IF NOT EXISTS tend
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tend;


-- ============================================
-- 1. RESET TABLES (DEVELOPMENT ONLY)
-- ============================================

DROP TABLE IF EXISTS food_preparation;
DROP TABLE IF EXISTS food_ingredients;
DROP TABLE IF EXISTS kitchen_items;

-- Previous architecture
DROP TABLE IF EXISTS recipe_ingredient;
DROP TABLE IF EXISTS recipes;

DROP TABLE IF EXISTS foods;
DROP TABLE IF EXISTS users;


-- ============================================
-- 2. USERS
-- ============================================

CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       cognito_user_id VARCHAR(255) NOT NULL UNIQUE
) ENGINE = InnoDB;


-- ============================================
-- 3. FOODS
-- Everything edible is a Food.
-- ============================================

CREATE TABLE foods (
                       food_id INT AUTO_INCREMENT PRIMARY KEY,

                       name VARCHAR(255) NOT NULL,
                       description TEXT,

    -- NULL means shared/system food
                       user_id INT NULL,

    -- Optional reference to the food copied
                       source_food_id INT NULL,

    -- DIRECT = entered nutritional values
    -- CALCULATED = derived from ingredients
                       nutrition_source VARCHAR(20) NOT NULL DEFAULT 'DIRECT',

    -- Nutrition per serving for DIRECT foods.
    -- CALCULATED foods derive these values.
                       calories DECIMAL(10,2) NULL,
                       protein DECIMAL(10,2) NULL,
                       carbs DECIMAL(10,2) NULL,
                       fat DECIMAL(10,2) NULL,

    -- Number of servings produced by this food
                       servings DECIMAL(10,2) NOT NULL DEFAULT 1,

    -- Description of one serving
                       serving_description VARCHAR(100),

                       CONSTRAINT chk_food_nutrition_source
                           CHECK (nutrition_source IN ('DIRECT', 'CALCULATED')),

                       CONSTRAINT chk_food_servings
                           CHECK (servings > 0),

                       CONSTRAINT chk_food_nutrition_values
                           CHECK (
                               (calories IS NULL OR calories >= 0)
                                   AND (protein IS NULL OR protein >= 0)
                                   AND (carbs IS NULL OR carbs >= 0)
                                   AND (fat IS NULL OR fat >= 0)
                               ),

                       CONSTRAINT fk_food_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(user_id)
                               ON DELETE RESTRICT,

                       CONSTRAINT fk_food_source
                           FOREIGN KEY (source_food_id)
                               REFERENCES foods(food_id)
                               ON DELETE SET NULL
) ENGINE = InnoDB;


-- ============================================
-- 4. FOOD INGREDIENTS
-- Foods can be composed of other Foods.
-- ============================================

CREATE TABLE food_ingredients (
                                  food_ingredient_id INT AUTO_INCREMENT PRIMARY KEY,

    -- The food being prepared
                                  parent_food_id INT NOT NULL,

    -- A food used as an ingredient
                                  ingredient_food_id INT NOT NULL,

                                  quantity DECIMAL(10,3) NOT NULL,
                                  unit VARCHAR(50) NOT NULL,

                                  CONSTRAINT chk_ingredient_quantity
                                      CHECK (quantity > 0),

                                  CONSTRAINT chk_ingredient_not_self
                                      CHECK (parent_food_id <> ingredient_food_id),

                                  CONSTRAINT fk_ingredient_parent
                                      FOREIGN KEY (parent_food_id)
                                          REFERENCES foods(food_id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_ingredient_food
                                      FOREIGN KEY (ingredient_food_id)
                                          REFERENCES foods(food_id)
                                          ON DELETE RESTRICT
) ENGINE = InnoDB;


-- ============================================
-- 5. FOOD PREPARATION
-- Optional preparation information.
-- ============================================

CREATE TABLE food_preparation (
                                  food_id INT PRIMARY KEY,

                                  prep_time_minutes INT NULL,
                                  cook_time_minutes INT NULL,

                                  preparation_method VARCHAR(100),
                                  instructions TEXT,

                                  CONSTRAINT chk_prep_time
                                      CHECK (
                                          prep_time_minutes IS NULL
                                              OR prep_time_minutes >= 0
                                          ),

                                  CONSTRAINT chk_cook_time
                                      CHECK (
                                          cook_time_minutes IS NULL
                                              OR cook_time_minutes >= 0
                                          ),

                                  CONSTRAINT fk_preparation_food
                                      FOREIGN KEY (food_id)
                                          REFERENCES foods(food_id)
                                          ON DELETE CASCADE
) ENGINE = InnoDB;


-- ============================================
-- 6. KITCHEN ITEMS
-- Users save Foods to their Kitchens.
-- ============================================

CREATE TABLE kitchen_items (
                               kitchen_item_id INT AUTO_INCREMENT PRIMARY KEY,

                               user_id INT NOT NULL,
                               food_id INT NOT NULL,

                               saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_kitchen_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(user_id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_kitchen_food
                                   FOREIGN KEY (food_id)
                                       REFERENCES foods(food_id)
                                       ON DELETE RESTRICT,

                               CONSTRAINT uk_kitchen_user_food
                                   UNIQUE (user_id, food_id)
) ENGINE = InnoDB;


-- ============================================
-- 7. SAMPLE USERS
-- ============================================

INSERT INTO users (user_id, cognito_user_id)
VALUES
    (1, 'test-user-001'),
    (2, 'test-user-002');


-- ============================================
-- 8. SAMPLE FOODS
-- ============================================

INSERT INTO foods (
    food_id,
    name,
    description,
    user_id,
    nutrition_source,
    calories,
    protein,
    carbs,
    fat,
    servings,
    serving_description
)
VALUES
    (
        1, 'Banana', NULL, NULL,
        'DIRECT', 105, 1.3, 27, 0.4,
        1, '1 medium banana'
    ),
    (
        2, 'Egg', NULL, NULL,
        'DIRECT', 72, 6.3, 0.4, 4.8,
        1, '1 large egg'
    ),
    (
        3, 'Whole Wheat Bread', NULL, NULL,
        'DIRECT', 80, 4, 14, 1,
        1, '1 slice'
    ),
    (
        4, 'Greek Yogurt', NULL, NULL,
        'DIRECT', 100, 17, 6, 0,
        1, '170 g'
    ),
    (
        5, 'Frozen Lasagna',
        'A convenient frozen meal.',
        NULL,
        'DIRECT', 420, 22, 48, 16,
        1, '1 package'
    ),
    (
        6, 'Eggs and Toast',
        'A simple homemade breakfast.',
        1,
        'CALCULATED', NULL, NULL, NULL, NULL,
        1, '1 plate'
    ),
    (
        7, 'Yogurt Bowl',
        'A quick breakfast or snack.',
        2,
        'CALCULATED', NULL, NULL, NULL, NULL,
        1, '1 bowl'
    );


-- ============================================
-- 9. SAMPLE FOOD INGREDIENTS
-- ============================================

INSERT INTO food_ingredients (
    parent_food_id,
    ingredient_food_id,
    quantity,
    unit
)
VALUES
    -- Eggs and Toast
    (6, 2, 2, 'egg'),
    (6, 3, 2, 'slice'),

    -- Yogurt Bowl
    (7, 4, 170, 'g'),
    (7, 1, 1, 'banana');


-- ============================================
-- 10. SAMPLE FOOD PREPARATION
-- ============================================

INSERT INTO food_preparation (
    food_id,
    prep_time_minutes,
    cook_time_minutes,
    preparation_method,
    instructions
)
VALUES
    (
        5, 0, 4, 'MICROWAVE',
        'Microwave according to package instructions.'
    ),
    (
        6, 2, 8, 'STOVETOP',
        'Cook the eggs and toast the bread.'
    ),
    (
        7, 5, 0, 'NO_COOK',
        'Combine the yogurt and sliced banana.'
    );


-- ============================================
-- 11. SAMPLE KITCHEN ITEMS
-- ============================================

INSERT INTO kitchen_items (
    kitchen_item_id,
    user_id,
    food_id
)
VALUES
    (1, 1, 6),
    (2, 1, 1),
    (3, 1, 5),
    (4, 2, 6),
    (5, 2, 7);


-- ============================================
-- 12. VERIFY DATABASE
-- ============================================

SHOW TABLES;

SELECT * FROM users;
SELECT * FROM foods;
SELECT * FROM food_ingredients;
SELECT * FROM food_preparation;
SELECT * FROM kitchen_items;


-- ============================================
-- 13. VIEW KITCHEN COLLECTIONS
-- ============================================

SELECT
    ki.kitchen_item_id,
    u.cognito_user_id,
    f.food_id,
    f.name,
    f.nutrition_source,
    f.calories,
    f.protein,
    f.carbs,
    f.fat,
    f.serving_description,
    ki.saved_at

FROM kitchen_items ki

         JOIN users u
              ON ki.user_id = u.user_id

         JOIN foods f
              ON ki.food_id = f.food_id

ORDER BY
    u.user_id,
    ki.kitchen_item_id;


-- ============================================
-- 14. VIEW FOOD COMPOSITION
-- ============================================

SELECT
    parent.name AS food_name,
    ingredient.name AS ingredient_name,
    fi.quantity,
    fi.unit

FROM food_ingredients fi

         JOIN foods parent
              ON fi.parent_food_id = parent.food_id

         JOIN foods ingredient
              ON fi.ingredient_food_id = ingredient.food_id

ORDER BY
    parent.food_id,
    fi.food_ingredient_id;


-- ============================================
-- 15. FIND QUICK FOODS
-- ============================================

SELECT
    f.food_id,
    f.name,
    fp.preparation_method,
    (
        COALESCE(fp.prep_time_minutes, 0)
            + COALESCE(fp.cook_time_minutes, 0)
        ) AS total_minutes

FROM foods f

         JOIN food_preparation fp
              ON f.food_id = fp.food_id

WHERE
    (
        COALESCE(fp.prep_time_minutes, 0)
            + COALESCE(fp.cook_time_minutes, 0)
        ) <= 10

ORDER BY total_minutes;
