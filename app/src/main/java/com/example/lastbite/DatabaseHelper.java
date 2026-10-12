package com.example.lastbite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "last_bite.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(),
                DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE pantry (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL)"
        );

        createRecipeTables(db);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Version 2 adds recipes without changing existing pantry records.
        if (oldVersion < 2) {
            createRecipeTables(db);
            seedRecipes(db);
        }
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE recipes (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "steps TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(_id) " +
                        "ON DELETE CASCADE)"
        );
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato Rice",
                "1. Rinse the rice.\n" +
                        "2. Chop the tomato and soften it in the oil.\n" +
                        "3. Add rice and water. Cover and simmer until cooked.",
                "rice|150|g;tomato|1|pieces;oil|10|ml;water|350|ml");

        addRecipe(db, "Egg Fried Rice",
                "1. Cook the rice in water, then drain.\n" +
                        "2. Heat oil and scramble the eggs until set.\n" +
                        "3. Add rice and stir-fry until hot.",
                "rice|150|g;egg|2|pieces;oil|10|ml;water|350|ml");

        addRecipe(db, "Pap and Eggs",
                "1. Bring water to a boil.\n" +
                        "2. Stir in maize meal and cook gently, stirring often.\n" +
                        "3. Fry the eggs in oil until set and serve with pap.",
                "maize meal|150|g;water|500|ml;egg|2|pieces;oil|10|ml");

        addRecipe(db, "Potato and Egg Scramble",
                "1. Peel and cut potatoes into small pieces.\n" +
                        "2. Fry gently in oil until tender.\n" +
                        "3. Add beaten eggs and stir until fully set.",
                "potato|2|pieces;egg|2|pieces;oil|15|ml");

        addRecipe(db, "Tomato Omelette",
                "1. Chop the tomato and soften it in oil.\n" +
                        "2. Beat the eggs and pour into the pan.\n" +
                        "3. Cook until set, then fold.",
                "tomato|1|pieces;egg|2|pieces;oil|10|ml");

        addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs and grate the cheese.\n" +
                        "2. Heat oil and add the eggs.\n" +
                        "3. Add cheese, cook until set and fold.",
                "egg|2|pieces;cheese|40|g;oil|10|ml");

        addRecipe(db, "Onion Omelette",
                "1. Slice the onion and soften it in oil.\n" +
                        "2. Add beaten eggs.\n" +
                        "3. Cook until set and fold.",
                "onion|1|pieces;egg|2|pieces;oil|10|ml");

        addRecipe(db, "French Toast",
                "1. Beat the egg with milk.\n" +
                        "2. Dip both bread slices into the mixture.\n" +
                        "3. Fry in butter until golden and the egg is cooked.",
                "bread|2|pieces;egg|1|pieces;milk|60|ml;butter|10|g");

        addRecipe(db, "Cheese Toastie",
                "1. Place cheese between the bread slices.\n" +
                        "2. Spread butter on the outside.\n" +
                        "3. Toast in a pan until golden and the cheese melts.",
                "bread|2|pieces;cheese|40|g;butter|10|g");

        addRecipe(db, "Tomato Cheese Toastie",
                "1. Slice the tomato and place it with cheese between bread.\n" +
                        "2. Butter the outside of the sandwich.\n" +
                        "3. Toast in a pan until golden and melted.",
                "bread|2|pieces;tomato|1|pieces;cheese|40|g;butter|10|g");

        addRecipe(db, "Peanut Butter Banana Toast",
                "1. Toast the bread.\n" +
                        "2. Spread with peanut butter.\n" +
                        "3. Slice the banana and place it on top.",
                "bread|2|pieces;peanut butter|30|g;banana|1|pieces");

        addRecipe(db, "Banana Oats",
                "1. Heat oats and milk gently, stirring until soft.\n" +
                        "2. Mash the banana.\n" +
                        "3. Stir the banana into the cooked oats.",
                "oats|50|g;milk|200|ml;banana|1|pieces");

        addRecipe(db, "Apple Cinnamon Oats",
                "1. Chop the apple into small pieces.\n" +
                        "2. Simmer apple, oats and milk until soft.\n" +
                        "3. Stir in cinnamon and serve.",
                "apple|1|pieces;oats|50|g;milk|200|ml;cinnamon|1|g");

        addRecipe(db, "Banana Pancakes",
                "1. Mash the banana and mix with egg and flour.\n" +
                        "2. Heat oil in a pan.\n" +
                        "3. Cook small pancakes on both sides until set.",
                "banana|1|pieces;egg|1|pieces;flour|30|g;oil|10|ml");

        addRecipe(db, "Tomato Pasta",
                "1. Cook pasta in boiling water and drain.\n" +
                        "2. Chop tomato and soften it in oil.\n" +
                        "3. Mix the pasta into the tomato sauce.",
                "pasta|150|g;water|1000|ml;tomato|2|pieces;oil|10|ml");

        addRecipe(db, "Cheesy Pasta",
                "1. Cook pasta in boiling water and drain.\n" +
                        "2. Stir milk into the hot pasta over low heat.\n" +
                        "3. Add grated cheese and stir until melted.",
                "pasta|150|g;water|1000|ml;milk|100|ml;cheese|50|g");

        addRecipe(db, "Cabbage and Onion Fry",
                "1. Shred the cabbage and slice the onion.\n" +
                        "2. Heat oil and soften the onion.\n" +
                        "3. Add cabbage and cook until tender.",
                "cabbage|200|g;onion|1|pieces;oil|15|ml");

        addRecipe(db, "Carrot and Potato Soup",
                "1. Peel and chop the potatoes and carrots.\n" +
                        "2. Simmer them in water until tender.\n" +
                        "3. Mash some vegetables into the liquid and stir.",
                "potato|2|pieces;carrot|2|pieces;water|600|ml");

        addRecipe(db, "Warm Beans on Toast",
                "1. Chop tomato and soften it in oil.\n" +
                        "2. Add cooked beans and heat thoroughly.\n" +
                        "3. Toast bread and spoon the beans on top.",
                "cooked beans|200|g;tomato|1|pieces;oil|10|ml;bread|2|pieces");

        addRecipe(db, "Spinach Scrambled Eggs",
                "1. Wash and chop the spinach.\n" +
                        "2. Wilt it in oil over medium heat.\n" +
                        "3. Add beaten eggs and stir until fully set.",
                "spinach|100|g;egg|2|pieces;oil|10|ml");
    }

    private void addRecipe(SQLiteDatabase db, String name,
                           String steps, String ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps", steps);

        long recipeId = db.insertOrThrow("recipes", null, recipeValues);

        // Each ingredient uses the format name|quantity|unit.
        for (String ingredient : ingredients.split(";")) {
            String[] parts = ingredient.split("\\|");

            ContentValues values = new ContentValues();
            values.put("recipe_id", recipeId);
            values.put("name", parts[0]);
            values.put("quantity", Double.parseDouble(parts[1]));
            values.put("unit", parts[2]);

            db.insertOrThrow("recipe_ingredients", null, values);
        }
    }

    public long addIngredient(String name, double quantity, String unit) {
        ContentValues values = ingredientValues(name, quantity, unit);
        return getWritableDatabase().insert("pantry", null, values);
    }

    public Cursor getAllIngredients() {
        return getReadableDatabase().query(
                "pantry", null, null, null, null, null,
                "name COLLATE NOCASE ASC, _id ASC");
    }

    public Cursor getIngredient(long id) {
        return getReadableDatabase().query(
                "pantry", null, "_id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
    }

    public int updateIngredient(long id, String name,
                                double quantity, String unit) {
        return getWritableDatabase().update(
                "pantry", ingredientValues(name, quantity, unit),
                "_id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteIngredient(long id) {
        return getWritableDatabase().delete(
                "pantry", "_id = ?",
                new String[]{String.valueOf(id)});
    }

    private ContentValues ingredientValues(
            String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        return values;
    }

    public Cursor getAllRecipes() {
        return getReadableDatabase().query(
                "recipes", null, null, null, null, null,
                "name COLLATE NOCASE ASC");
    }

    public Cursor getRecipe(long id) {
        return getReadableDatabase().query(
                "recipes", null, "_id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
    }

    public Cursor getRecipeIngredients(long recipeId) {
        return getReadableDatabase().query(
                "recipe_ingredients", null, "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, "_id ASC");
    }
}