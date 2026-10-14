# Last Bite — Smart Pantry Manager

Less waste. More taste.

Last Bite is a Java Android application that helps users keep track of
their pantry and find recipes they can prepare with the ingredients
they already have.

## Features

- Add ingredients with a name, quantity and unit.
- View pantry ingredients using a custom ListView adapter.
- Tap an ingredient to edit it.
- Press and hold an ingredient to delete it with confirmation.
- Keep pantry data after closing and reopening the app.
- Find recipes only when every required ingredient is available
  in a sufficient quantity.
- View recipe ingredients and cooking instructions.
- Show or hide pantry tips through a saved Settings preference.
- Includes 20 recipes stored in the local database.

## Technologies

- Java for application logic
- XML for screen layouts
- SQLiteOpenHelper for database creation and upgrades
- CursorAdapter for the pantry list
- Intents for navigation and passing the selected recipe ID
- SharedPreferences for the pantry tips setting

## Setup and Running

1. Clone or download this repository.
2. Open the LastBite project folder in Android Studio.
3. Allow Gradle sync to finish.
4. Install any Android SDK components requested by the project.
5. Start an Android emulator or connect a compatible Android device
   with USB debugging enabled.
6. Select the app run configuration and the device.
7. Click Run.

The pantry starts empty on a fresh installation. The database
automatically creates and seeds the 20 recipes.

The application works offline. Internet access may be needed when
setting up Android Studio and downloading build dependencies.

## Screens

1. My Pantry — lists saved ingredients and opens the other screens.
2. Add/Edit Ingredient — validates and saves pantry entries.
3. Cook Now — lists recipes that match the available pantry quantities.
4. Recipe Details — displays ingredients and cooking instructions.
5. Settings — saves the option to show or hide pantry tips.

## Database Design

The SQLite database is named last_bite.db and uses three tables:

- pantry: ingredient ID, name, quantity and unit
- recipes: recipe ID, name and cooking steps
- recipe_ingredients: ingredient ID, recipe ID, name, quantity and unit

Each recipe can have several required ingredients. The recipe_id
foreign key connects recipe_ingredients to recipes.

SQLite suits this application because pantry entries and recipe
ingredients are structured, related data. It supports local storage,
queries and CRUD operations without an account or server.

Database version 2 adds the recipe tables and seed data while
preserving existing pantry entries.

SharedPreferences stores the single pantry tips setting because it
is a simple true-or-false preference.

## Recipe Matching

A recipe is suggested only when all its required ingredients have
enough quantity in the pantry.

Before comparing quantities, the matcher:

- Trims ingredient names and ignores letter case.
- Normalises repeated spaces.
- Recognises selected plural forms, such as tomatoes and eggs.
- Converts kilograms to grams and litres to millilitres.
- Adds together duplicate pantry entries with matching names
  and compatible units.

Pieces are compared with pieces. The app does not convert weight
or volume into a number of pieces.

## Example: Tomato Rice

Add these pantry entries:

- Rice: 150 g
- Tomatoes: 1 pieces
- Oil: 10 ml
- Water: 0.35 l

Open Cook Now. Tomato Rice should appear.

Change the water quantity to 0.34 l. Tomato Rice should disappear
because 340 ml is below its required 350 ml.

Restore the water to 0.35 l. The recipe should appear again.

## Input Validation

- Ingredient names cannot be blank.
- Quantities must be valid, finite numbers greater than zero.
- Quantities entered as pieces must be whole numbers.
- An unsuccessful database operation does not show a success message.

## Current Limitations

- Ingredient aliases cover selected names rather than every possible
  spelling or synonym.
- Recipe quantities are fixed; serving sizes cannot be adjusted.
- Preparing a recipe does not automatically subtract pantry quantities.
- Expiry dates and nutrition information are not tracked.