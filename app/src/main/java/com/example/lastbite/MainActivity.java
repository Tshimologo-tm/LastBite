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
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the list when returning from the ingredient form.
        loadPantry();
    }

    private void loadPantry() {
        try {
            Cursor cursor = databaseHelper.getAllIngredients();
            boolean isEmpty = cursor.getCount() == 0;

            // The adapter closes its previous cursor when it is replaced.
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