
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
DROP TABLE IF EXISTS nutrition;
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

    -- Number of servings produced
                       servings DECIMAL(10,2) NOT NULL DEFAULT 1,

    -- Description of one serving
                       serving_description VARCHAR(100),

                       CONSTRAINT chk_food_servings
                           CHECK (servings > 0),

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
-- 4. NUTRITION
-- Nutritional information for a Food.
-- ============================================

CREATE TABLE nutrition (
                           nutrition_id INT AUTO_INCREMENT PRIMARY KEY,

                           food_id INT NOT NULL UNIQUE,

    -- MANUAL = entered by a user
    -- REFERENCE = external nutritional database
    -- CALCULATED = derived from ingredients
                           source VARCHAR(30) NOT NULL,

    -- Nutritional values per serving
                           calories DECIMAL(10,2),
                           protein DECIMAL(10,2),
                           carbs DECIMAL(10,2),
                           fat DECIMAL(10,2),

                           CONSTRAINT chk_nutrition_source
                               CHECK (
                                   source IN (
                                              'MANUAL',
                                              'REFERENCE',
                                              'CALCULATED'
                                       )
                                   ),

                           CONSTRAINT chk_nutrition_values
                               CHECK (
                                   (calories IS NULL OR calories >= 0)
                                       AND (protein IS NULL OR protein >= 0)
                                       AND (carbs IS NULL OR carbs >= 0)
                                       AND (fat IS NULL OR fat >= 0)
                                   ),

                           CONSTRAINT fk_nutrition_food
                               FOREIGN KEY (food_id)
                                   REFERENCES foods(food_id)
                                   ON DELETE CASCADE
) ENGINE = InnoDB;


-- ============================================
-- 5. FOOD INGREDIENTS
-- Recipe ingredients reference foods in the separate USDA database.
-- ============================================

CREATE TABLE food_ingredients (
                                  food_ingredient_id INT AUTO_INCREMENT PRIMARY KEY,

    -- The food being prepared
                                  parent_food_id INT NOT NULL,

    -- USDA reference; validated by the application, not a cross-database foreign key
                                  usda_fdc_id BIGINT NOT NULL,

                                  quantity DECIMAL(10,3) NOT NULL,
                                  unit VARCHAR(50) NOT NULL,

                                  CONSTRAINT chk_ingredient_quantity
                                      CHECK (quantity > 0),

                                  CONSTRAINT fk_ingredient_parent
                                      FOREIGN KEY (parent_food_id)
                                          REFERENCES foods(food_id)
                                          ON DELETE CASCADE
) ENGINE = InnoDB;


-- ============================================
-- 6. FOOD PREPARATION
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
-- 7. KITCHEN ITEMS
-- Users save Foods to their Kitchens.
-- ============================================

CREATE TABLE kitchen_items (
                               kitchen_item_id INT AUTO_INCREMENT PRIMARY KEY,

                               user_id INT NOT NULL,
                               food_id INT NOT NULL,

                               saved_at TIMESTAMP NOT NULL
                                   DEFAULT CURRENT_TIMESTAMP,

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
-- 8. SAMPLE USERS
-- ============================================

INSERT INTO users (user_id, cognito_user_id)
VALUES
    (1, 'test-user-001'),
    (2, 'test-user-002');


-- ============================================
-- 9. SAMPLE FOODS
-- ============================================

INSERT INTO foods (
    food_id,
    name,
    description,
    user_id,
    servings,
    serving_description
)
VALUES
    (
        1,
        'Banana',
        NULL,
        NULL,
        1,
        '1 medium banana'
    ),
    (
        2,
        'Egg',
        NULL,
        NULL,
        1,
        '1 large egg'
    ),
    (
        3,
        'Whole Wheat Bread',
        NULL,
        NULL,
        1,
        '1 slice'
    ),
    (
        4,
        'Greek Yogurt',
        NULL,
        NULL,
        1,
        '170 g'
    ),
    (
        5,
        'Frozen Lasagna',
        'A convenient frozen meal.',
        NULL,
        1,
        '1 package'
    ),
    (
        6,
        'Eggs and Toast',
        'A simple homemade breakfast.',
        1,
        1,
        '1 plate'
    ),
    (
        7,
        'Yogurt Bowl',
        'A quick breakfast or snack.',
        2,
        1,
        '1 bowl'
    );


-- ============================================
-- 10. SAMPLE NUTRITION
-- ============================================

INSERT INTO nutrition (
    food_id,
    source,
    calories,
    protein,
    carbs,
    fat
)
VALUES
    (
        1, 'REFERENCE',
        105, 1.3, 27, 0.4
    ),
    (
        2, 'REFERENCE',
        72, 6.3, 0.4, 4.8
    ),
    (
        3, 'REFERENCE',
        80, 4, 14, 1
    ),
    (
        4, 'REFERENCE',
        100, 17, 6, 0
    ),
    (
        5, 'MANUAL',
        420, 22, 48, 16
    ),
    (
        6, 'CALCULATED',
        NULL, NULL, NULL, NULL
    ),
    (
        7, 'CALCULATED',
        NULL, NULL, NULL, NULL
    );


-- ============================================
-- 11. SAMPLE FOOD INGREDIENTS
-- ============================================

-- Ingredient examples must use real IDs from the local USDA database.
-- No Tend food IDs are seeded as USDA references.


-- ============================================
-- 12. SAMPLE FOOD PREPARATION
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
        5,
        0,
        4,
        'MICROWAVE',
        'Microwave according to package instructions.'
    ),
    (
        6,
        2,
        8,
        'STOVETOP',
        'Cook the eggs and toast the bread.'
    ),
    (
        7,
        5,
        0,
        'NO_COOK',
        'Combine the yogurt and sliced banana.'
    );


-- ============================================
-- 13. SAMPLE KITCHEN ITEMS
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
-- 14. VERIFY DATABASE
-- ============================================

SHOW TABLES;

SELECT * FROM users;
SELECT * FROM foods;
SELECT * FROM nutrition;
SELECT * FROM food_ingredients;
SELECT * FROM food_preparation;
SELECT * FROM kitchen_items;


-- ============================================
-- 15. VIEW KITCHEN COLLECTIONS
-- ============================================

SELECT
    ki.kitchen_item_id,
    u.cognito_user_id,
    f.food_id,
    f.name,
    n.source AS nutrition_source,
    n.calories,
    n.protein,
    n.carbs,
    n.fat,
    f.serving_description,
    ki.saved_at

FROM kitchen_items ki

         JOIN users u
              ON ki.user_id = u.user_id

         JOIN foods f
              ON ki.food_id = f.food_id

         LEFT JOIN nutrition n
                   ON f.food_id = n.food_id

ORDER BY
    u.user_id,
    ki.kitchen_item_id;


-- ============================================
-- 16. VIEW FOOD COMPOSITION
-- ============================================

SELECT
    parent.name AS food_name,
    fi.usda_fdc_id,
    fi.quantity,
    fi.unit

FROM food_ingredients fi

         JOIN foods parent
              ON fi.parent_food_id = parent.food_id

ORDER BY
    parent.food_id,
    fi.food_ingredient_id;


-- ============================================
-- 17. FIND QUICK FOODS
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
