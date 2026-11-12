# Tend

Tend is an adaptable personal food-life planning system.

It helps people plan, track, and make decisions about everything they eat: home-cooked meals, simple foods, snacks, leftovers, restaurant meals, takeout, groceries, and meals they have not decided on yet.

Recipes matter, but they are only one part of the system. A person should be able to plan a homemade turkey sandwich, log a Jersey Mike’s sub, replace dinner with Chinese takeout, add a grocery-store premade meal, track snacks, and still have one useful picture of their food, schedule, budget, nutrition, groceries, and preferences.

The goal is to make food planning easier and more personal over time not to force every food decision into a tedious recipe.

## The Problem

Food decisions are spread across too many disconnected places.

Recipes are saved in browser tabs. Grocery lists live separately from meal plans. Restaurant and takeout spending are disconnected from food budgets. Nutrition tracking can become tedious. Calendars do not understand meals. Most meal-planning tools assume every meal is a recipe someone needs to cook from scratch.

Real life does not work that way.

People eat leftovers. They grab a bagel and coffee. They make a quick sandwich. They buy a prepared meal from the grocery store. They order takeout because they are tired. They cook when they have time. Plans change based on budget, cravings, energy, schedule, dietary needs, and what is already in the kitchen.

Tend brings those decisions together into one system.

## Product Vision

Provisions should help users:

- Plan breakfast, lunch, dinner, snacks, and custom meal categories on a calendar
- Track home-cooked meals, recipes, simple foods, leftovers, restaurant meals, takeout, delivery, and grocery-store prepared foods
- Treat recipes as one meal option, not a requirement
- Save, browse, search, organize, and favorite recipes
- Create recipes manually or with AI assistance
- Track planned meals versus what the user actually ate
- Build grocery lists from meals the user plans to prepare at home
- Avoid adding grocery ingredients for restaurant meals, takeout, leftovers, or prepared foods
- Account for serving size, household needs, dietary restrictions, allergies, disliked ingredients, cooking time, budget, and nutrition goals
- Track food spending across groceries, restaurants, takeout, and other purchases
- Suggest recipes, meals, grocery decisions, and shopping days based on each user’s actual habits
- Improve recommendations as users save food, plan meals, log decisions, and give feedback
- Eventually connect with grocery-shopping, delivery, restaurant, or retailer services where partner APIs allow it

## Food Entries, Not Just Recipes

The core of Provisions is a **food entry**.

A food entry belongs on a date, a time, and a category such as breakfast, lunch, dinner, snack, or a custom category. It can represent:

- A saved recipe
- A homemade meal
- A simple food, such as a turkey sandwich or cereal
- Leftovers
- A restaurant meal
- Takeout or delivery
- A grocery-store prepared meal
- A snack
- A planned but undecided meal
- A meal suggested or created by the AI

Each food entry can contain information that makes sense for that decision:

- Planned or completed date and time
- Meal category
- Recipe, restaurant, store, or merchant
- Food items
- Serving size
- Expected or actual cost
- Nutrition information or estimates
- Ingredients and grocery needs for home-prepared food
- Notes, ratings, substitutions, and feedback
- Whether the entry was planned, completed, changed, skipped, or replaced
- Whether the user wants similar meals suggested in the future

For example, a planned Jersey Mike’s lunch should affect a user’s calendar, budget, nutrition picture, and preference history. It should not add sandwich ingredients to the grocery list.

## Conversational AI Assistant

The Tend chatbot is not just a recipe chatbot. It should act as a conversational control surface for the user’s food life.

Users should be able to say things like:

- “Add a Jersey Mike’s turkey sub for lunch today. It was about $14.”
- “I got Chinese takeout for dinner last night: orange chicken, rice, and crab rangoon.”
- “Move tonight’s dinner to tomorrow. We ordered pizza instead.”
- “Add the Costco chicken Caesar wrap I had for lunch.”
- “I bought rotisserie chicken and pasta salad for dinner.”
- “Plan cheap dinners for this week.”
- “I spent too much eating out this month. What can I make with what I already have?”
- “I need quick lunches that are not chicken again.”
- “Add snacks for my work days.”
- “What should I grocery shop for this weekend?”

The chatbot should be able to:

- Add, update, move, replace, and remove food entries
- Log completed meals and snacks
- Create or update meal plans
- Explain why a meal, recipe, or grocery item was recommended
- Generate grocery lists from home-prepared meals
- Account for time, serving size, budget, nutrition, preferences, and existing plans
- Identify conflicts or gaps in a meal plan
- Learn from feedback without forcing users to manually maintain every preference

## Receipt Scanning and Food Capture

Provisions should make it easy to capture food decisions that happen outside the kitchen.

A user should be able to upload a receipt from a grocery store, restaurant, takeout order, or delivery service. The application can use OCR and AI to identify the merchant, date, items, and cost, then propose food, grocery, budget, or meal-history entries.

The system should never blindly trust a receipt scan.

```text
Receipt or screenshot uploaded
        ↓
OCR and AI identify merchant, date, items, and prices
        ↓
Provisions proposes food entries, grocery items, and budget updates
        ↓
User confirms or corrects unclear information
        ↓
Confirmed data updates meal history, budget, groceries, and preferences
```

This allows a user to capture a grocery-store prepared meal, a restaurant order, or takeout without manually typing everything. The user remains in control of what is saved.

## Personalization and AI

AI should coordinate the food-planning system instead of acting as a separate chat feature.

It should help users make realistic decisions based on things such as:

- Dietary restrictions and allergies
- Favorite foods and disliked ingredients
- Household size and serving preferences
- Budget preferences
- Nutrition goals
- Saved recipes and favorites
- Meal history
- Restaurant and takeout habits
- Feedback, skipped meals, substitutions, and ratings
- Grocery history and pantry information
- Schedule, available time, and cooking skill

Personalization will use two complementary kinds of information:

1. **Structured information** such as dietary restrictions, allergies, household size, budgets, nutrition goals, favorites, food history, and meal plans.
2. **Semantic recall** using embeddings to find relevant past feedback, notes, and preferences when a user asks for a new recommendation.

Embeddings are not the source of truth. Users should be able to view, edit, and remove the information that affects their recommendations.

## Product Roadmap

### Phase 1: Personal Food Planning Foundation

- User accounts and private food data
- Calendar-based planning for meals and snacks
- Food entries for recipes, homemade meals, simple foods, leftovers, restaurant meals, takeout, prepared foods, and undecided plans
- Saved and favorited recipes
- Manual recipe creation and AI-assisted recipe generation
- Basic grocery lists for planned home-prepared meals
- Dietary restrictions, allergies, disliked ingredients, and stated preferences
- Conversational creation and editing of food entries

### Phase 2: Grocery, Budget, and Food History

- Planned versus completed food entries
- Grocery, restaurant, takeout, and delivery spending
- Serving-size adjustments
- Grocery lists that combine ingredients across planned home-prepared meals
- Pantry-aware planning
- Budget constraints and food-budget tracking
- Suggested shopping days and consolidated shopping trips
- Receipt scanning with user-confirmed food, grocery, and cost extraction

### Phase 3: Nutrition and Adaptive Recommendations

- Nutrition goals and optional meal logging
- Personalized meal, recipe, restaurant, and grocery suggestions
- Recommendations based on favorites, food history, feedback, budget, nutrition goals, and recent choices
- AI-assisted planning across multiple days
- Transparent preference profiles users can correct
- Suggestions for variety, convenience, leftovers, grocery use, and realistic schedules
- Embedding-based recall when it improves recommendation quality

### Phase 4: Connected Food Services

- Grocery-store and delivery-service integrations where supported
- Grocery-cart handoff or ordering workflows
- Restaurant and takeout planning integrations
- Optional nutrition or health-data integrations with user permission
- Shared household grocery lists and meal planning
- More advanced automation that remains transparent and user-controlled

## Guiding Principles

- **Everything counts as food.** A meal does not need to be a recipe to be worth planning.
- **Recipes are optional.** They are useful, but not the center of the product.
- **Real life beats ideal planning.** Snacks, leftovers, splurges, takeout, prepared foods, and changed plans are normal.
- **The chatbot should do useful work.** It should be able to update the system, not only give suggestions.
- **Grocery lists should be practical.** Only home-prepared meals should create ingredient needs.
- **The system should improve over time.** Recommendations should become more useful as users make decisions and give feedback.
- **Users stay in control.** People should understand, edit, and remove the data that affects recommendations.
- **AI should reduce effort.** It should make food decisions easier, not create more work.

## Author

Drew Weise
