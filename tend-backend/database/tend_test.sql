
-- ============================================
-- TEND DATABASE
-- Complete development/testing setup
-- MySQL 8.0.16+
-- ============================================

-- ============================================
-- 1. RESET DATABASE
-- ============================================

DROP DATABASE IF EXISTS tend_test;

CREATE DATABASE tend_test
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tend_test;


-- ============================================
-- 2. CREATE TABLES
-- ============================================

-- USERS
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,

                       cognito_user_id VARCHAR(255)
                           NOT NULL UNIQUE
) ENGINE = InnoDB;


-- FOODS
CREATE TABLE foods (
                       food_id INT AUTO_INCREMENT PRIMARY KEY,

                       name VARCHAR(255) NOT NULL
) ENGINE = InnoDB;


-- RECIPES
CREATE TABLE recipes (
                         recipe_id INT AUTO_INCREMENT PRIMARY KEY,

                         title VARCHAR(255) NOT NULL,

                         description TEXT,

                         instructions TEXT,

                         servings INT NOT NULL DEFAULT 1,

                         user_id INT NOT NULL,

                         CONSTRAINT chk_recipe_servings
                             CHECK (servings > 0),

                         CONSTRAINT fk_recipe_user
                             FOREIGN KEY (user_id)
                                 REFERENCES users(user_id)
                                 ON DELETE RESTRICT
) ENGINE = InnoDB;


-- RECIPE INGREDIENTS
CREATE TABLE recipe_ingredient (
                                   recipe_ingredient_id INT
                                       AUTO_INCREMENT PRIMARY KEY,

                                   recipe_id INT NOT NULL,

                                   food_id INT NOT NULL,

                                   amount VARCHAR(255) NOT NULL,

                                   CONSTRAINT fk_recipe_ingredient_recipe
                                       FOREIGN KEY (recipe_id)
                                           REFERENCES recipes(recipe_id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_recipe_ingredient_food
                                       FOREIGN KEY (food_id)
                                           REFERENCES foods(food_id)
                                           ON DELETE RESTRICT
) ENGINE = InnoDB;


-- KITCHEN ITEMS
CREATE TABLE kitchen_items (
                               kitchen_item_id INT
                                   AUTO_INCREMENT PRIMARY KEY,

                               user_id INT NOT NULL,

                               food_id INT NULL,

                               recipe_id INT NULL,

                               CONSTRAINT fk_kitchen_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(user_id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_kitchen_food
                                   FOREIGN KEY (food_id)
                                       REFERENCES foods(food_id)
                                       ON DELETE RESTRICT,

                               CONSTRAINT fk_kitchen_recipe
                                   FOREIGN KEY (recipe_id)
                                       REFERENCES recipes(recipe_id)
                                       ON DELETE CASCADE,

                               CONSTRAINT chk_kitchen_item_type
                                   CHECK (
                                       (food_id IS NOT NULL AND recipe_id IS NULL)
                                           OR
                                       (food_id IS NULL AND recipe_id IS NOT NULL)
                                       ),

                               CONSTRAINT uk_kitchen_user_food
                                   UNIQUE (user_id, food_id),

                               CONSTRAINT uk_kitchen_user_recipe
                                   UNIQUE (user_id, recipe_id)
) ENGINE = InnoDB;


-- ============================================
-- 3. INSERT TEST DATA
-- ============================================

-- TEST USERS
INSERT INTO users (user_id, cognito_user_id)
VALUES
    (1, 'test-user-001'),
    (2, 'test-user-002');


-- TEST FOODS
INSERT INTO foods (food_id, name)
VALUES
    (1, 'Banana'),
    (2, 'Egg'),
    (3, 'Whole Wheat Bread'),
    (4, 'Greek Yogurt'),
    (5, 'Frozen Lasagna');


-- TEST RECIPES
INSERT INTO recipes (
    recipe_id,
    title,
    description,
    instructions,
    servings,
    user_id
)
VALUES
    (
        1,
        'Eggs and Toast',
        'A simple breakfast.',
        'Cook eggs and toast bread.',
        1,
        1
    ),
    (
        2,
        'Yogurt Bowl',
        'A quick breakfast or snack.',
        NULL,
        1,
        2
    );


-- TEST RECIPE INGREDIENTS
INSERT INTO recipe_ingredient (
    recipe_id,
    food_id,
    amount
)
VALUES
    (1, 2, '2 eggs'),
    (1, 3, '2 slices'),
    (2, 4, '1 cup'),
    (2, 1, '1 banana');


-- TEST KITCHEN ITEMS
INSERT INTO kitchen_items (
    user_id,
    food_id,
    recipe_id
)
VALUES
    (1, NULL, 1),
    (1, 1, NULL),
    (1, 5, NULL),
    (2, NULL, 1),
    (2, NULL, 2);


-- ============================================
-- 4. VERIFY DATABASE
-- ============================================

SHOW TABLES;

SELECT * FROM users;
SELECT * FROM foods;
SELECT * FROM recipes;
SELECT * FROM recipe_ingredient;
SELECT * FROM kitchen_items;


-- ============================================
-- 5. VERIFY KITCHEN CONTENTS
-- ============================================

SELECT
    ki.kitchen_item_id,
    u.cognito_user_id,
    CASE
        WHEN ki.food_id IS NOT NULL THEN 'Food'
        ELSE 'Recipe'
        END AS item_type,
    COALESCE(f.name, r.title) AS item_name

FROM kitchen_items ki

         JOIN users u
              ON ki.user_id = u.user_id

         LEFT JOIN foods f
                   ON ki.food_id = f.food_id

         LEFT JOIN recipes r
                   ON ki.recipe_id = r.recipe_id

ORDER BY
    u.user_id,
    ki.kitchen_item_id;
