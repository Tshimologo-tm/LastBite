package com.example.lastbite;

import android.database.Cursor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    private final DatabaseHelper databaseHelper;

    public RecipeMatcher(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public ArrayList<Long> getSuggestedRecipeIds() {
        ArrayList<Long> suggestedIds = new ArrayList<>();
        Map<String, BigDecimal> pantryAmounts;

        try (Cursor pantry = databaseHelper.getAllIngredients()) {
            pantryAmounts = readAmounts(pantry);
        }

        try (Cursor recipes = databaseHelper.getAllRecipes()) {
            while (recipes.moveToNext()) {
                long recipeId = recipes.getLong(
                        recipes.getColumnIndexOrThrow("_id"));

                Map<String, BigDecimal> requiredAmounts;

                try (Cursor ingredients =
                             databaseHelper.getRecipeIngredients(recipeId)) {
                    requiredAmounts = readAmounts(ingredients);
                }

                if (hasEveryIngredient(pantryAmounts, requiredAmounts)) {
                    suggestedIds.add(recipeId);
                }
            }
        }

        return suggestedIds;
    }

    private Map<String, BigDecimal> readAmounts(Cursor cursor) {
        Map<String, BigDecimal> amounts = new HashMap<>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("name"));
            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("quantity"));
            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow("unit"));

            String key = normaliseName(name) + "|" + baseUnit(unit);
            BigDecimal amount = baseQuantity(quantity, unit);

            // Combine repeated ingredients measured in compatible units.
            BigDecimal previous = amounts.get(key);
            amounts.put(key, previous == null
                    ? amount : previous.add(amount));
        }

        return amounts;
    }

    private boolean hasEveryIngredient(
            Map<String, BigDecimal> pantry,
            Map<String, BigDecimal> required) {

        if (required.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, BigDecimal> ingredient :
                required.entrySet()) {

            BigDecimal available = pantry.get(ingredient.getKey());

            // A missing ingredient or insufficient amount rejects the recipe.
            if (available == null
                    || available.compareTo(ingredient.getValue()) < 0) {
                return false;
            }
        }

        return true;
    }

    private String normaliseName(String name) {
        String cleaned = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        switch (cleaned) {
            case "tomatoes":
                return "tomato";
            case "potatoes":
                return "potato";
            case "eggs":
                return "egg";
            case "onions":
                return "onion";
            case "bananas":
                return "banana";
            case "apples":
                return "apple";
            case "carrots":
                return "carrot";
            case "cabbages":
                return "cabbage";
            case "cooked bean":
                return "cooked beans";
            case "maize flour":
            case "mealie meal":
                return "maize meal";
            default:
                return cleaned;
        }
    }

    private String baseUnit(String unit) {
        String cleaned = unit.trim().toLowerCase(Locale.ROOT);

        switch (cleaned) {
            case "kg":
            case "g":
                return "g";
            case "l":
            case "ml":
                return "ml";
            default:
                return cleaned;
        }
    }

    private BigDecimal baseQuantity(double quantity, String unit) {
        BigDecimal amount = BigDecimal.valueOf(quantity);
        String cleaned = unit.trim().toLowerCase(Locale.ROOT);

        if (cleaned.equals("kg") || cleaned.equals("l")) {
            return amount.multiply(BigDecimal.valueOf(1000));
        }

        return amount;
    }
}