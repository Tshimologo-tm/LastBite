package com.example.lastbite;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private TextView txtEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    view.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        databaseHelper = new DatabaseHelper(this);
        pantryAdapter = new PantryAdapter(this);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        ListView listPantry = findViewById(R.id.listPantry);
        listPantry.setAdapter(pantryAdapter);

        findViewById(R.id.btnAddIngredient).setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });

        // Open recipes that match the current pantry.
        findViewById(R.id.btnRecipes).setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnSettings).setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });

        // Pass the selected ingredient ID to the form for editing.
        listPantry.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Intent intent = new Intent(
                            MainActivity.this, AddIngredientActivity.class);
                    intent.putExtra("ingredient_id", id);
                    startActivity(intent);
                });

        // A long press opens a confirmation for the selected ingredient.
        listPantry.setOnItemLongClickListener(
                (parent, view, position, id) -> {
                    Cursor item = (Cursor) pantryAdapter.getItem(position);
                    String name = item.getString(
                            item.getColumnIndexOrThrow("name"));
                    confirmDelete(id, name);
                    return true;
                });
    }

    private void confirmDelete(long id, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + name + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    try {
                        int deletedRows =
                                databaseHelper.deleteIngredient(id);

                        if (deletedRows == 1) {
                            Toast.makeText(this, "Ingredient deleted",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Ingredient no longer exists",
                                    Toast.LENGTH_SHORT).show();
                        }

                        loadPantry();

                    } catch (SQLiteException exception) {
                        Toast.makeText(this, "Could not delete ingredient",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();

        boolean showTips = getSharedPreferences(
                "last_bite_settings", MODE_PRIVATE
        ).getBoolean("show_pantry_tips", true);

        findViewById(R.id.txtPantryTips).setVisibility(
                showTips ? android.view.View.VISIBLE : android.view.View.GONE
        );
    }

    private void loadPantry() {
        try {
            Cursor cursor = databaseHelper.getAllIngredients();
            boolean isEmpty = cursor.getCount() == 0;
            pantryAdapter.changeCursor(cursor);

            txtEmptyPantry.setText(
                    "Your pantry is empty. Add your first ingredient!");
            txtEmptyPantry.setVisibility(
                    isEmpty ? View.VISIBLE : View.GONE);

        } catch (SQLiteException exception) {
            Toast.makeText(this, "Could not load pantry",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (pantryAdapter != null) {
            pantryAdapter.changeCursor(null);
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}