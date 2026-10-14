package com.example.lastbite;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> recipeAdapter;
    private TextView txtRecipeCount;
    private TextView txtNoRecipes;
    private final ArrayList<String> recipeNames = new ArrayList<>();
    private final ArrayList<Long> recipeIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    view.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        databaseHelper = new DatabaseHelper(this);
        txtRecipeCount = findViewById(R.id.txtRecipeCount);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);

        recipeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, recipeNames) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                TextView text = row.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#253A2E"));
                text.setTextSize(18);
                return row;
            }
        };

        ListView listRecipes = findViewById(R.id.listSuggestedRecipes);
        listRecipes.setAdapter(recipeAdapter);

        findViewById(R.id.btnBackToPantry)
                .setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        recipeNames.clear();
        recipeIds.clear();

        try {
            RecipeMatcher matcher = new RecipeMatcher(databaseHelper);
            ArrayList<Long> matchingIds = matcher.getSuggestedRecipeIds();

            // Display only recipes approved by the strict matcher.
            for (long id : matchingIds) {
                try (Cursor recipe = databaseHelper.getRecipe(id)) {
                    if (recipe.moveToFirst()) {
                        recipeIds.add(id);
                        recipeNames.add(recipe.getString(
                                recipe.getColumnIndexOrThrow("name")));
                    }
                }
            }

            int count = recipeNames.size();
            txtRecipeCount.setText(count == 1
                    ? "1 recipe ready to cook"
                    : count + " recipes ready to cook");

            txtNoRecipes.setText(
                    "No recipes match your pantry yet. Add more ingredients " +
                            "or check your quantities and units.");
            txtNoRecipes.setVisibility(
                    count == 0 ? View.VISIBLE : View.GONE);

        } catch (SQLiteException exception) {
            recipeNames.clear();
            recipeIds.clear();
            txtRecipeCount.setText("Could not check your pantry");
            txtNoRecipes.setText("Please go back and try again.");
            txtNoRecipes.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Could not load recipes",
                    Toast.LENGTH_SHORT).show();
        }

        recipeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}