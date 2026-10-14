package com.example.lastbite;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;

public class RecipeDetailsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_details);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        databaseHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBackToSuggestions)
                .setOnClickListener(view -> finish());

        long recipeId = getIntent().getLongExtra("recipe_id", -1);

        if (recipeId == -1) {
            Toast.makeText(
                    this, "Recipe not found.", Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        loadRecipe(recipeId);
    }

    private void loadRecipe(long recipeId) {
        TextView recipeName = findViewById(R.id.txtRecipeName);
        TextView recipeIngredients =
                findViewById(R.id.txtRecipeIngredients);
        TextView recipeSteps = findViewById(R.id.txtRecipeSteps);

        try {
            try (Cursor recipe = databaseHelper.getRecipe(recipeId)) {
                if (!recipe.moveToFirst()) {
                    Toast.makeText(
                            this, "Recipe not found.", Toast.LENGTH_SHORT
                    ).show();
                    finish();
                    return;
                }

                recipeName.setText(
                        recipe.getString(
                                recipe.getColumnIndexOrThrow("name")
                        )
                );

                recipeSteps.setText(
                        recipe.getString(
                                recipe.getColumnIndexOrThrow("steps")
                        )
                );
            }

            StringBuilder ingredients = new StringBuilder();

            try (Cursor cursor =
                         databaseHelper.getRecipeIngredients(recipeId)) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    );

                    double quantity = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("quantity")
                    );

                    String unit = cursor.getString(
                            cursor.getColumnIndexOrThrow("unit")
                    );

                    String amount = BigDecimal.valueOf(quantity)
                            .stripTrailingZeros()
                            .toPlainString();

                    if (ingredients.length() > 0) {
                        ingredients.append("\n");
                    }

                    ingredients.append("• ")
                            .append(name)
                            .append(" — ")
                            .append(amount)
                            .append(" ")
                            .append(unit);
                }
            }

            recipeIngredients.setText(ingredients.toString());

        } catch (SQLiteException exception) {
            Toast.makeText(
                    this,
                    "Could not load the recipe. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}