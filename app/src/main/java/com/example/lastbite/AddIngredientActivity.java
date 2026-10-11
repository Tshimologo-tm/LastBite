package com.example.lastbite;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText edtIngredientName;
    private EditText edtQuantity;
    private Spinner spinnerUnit;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_ingredient);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    Insets keyboard = insets.getInsets(
                            WindowInsetsCompat.Type.ime());

                    view.setPadding(systemBars.left, systemBars.top,
                            systemBars.right,
                            Math.max(systemBars.bottom, keyboard.bottom));
                    return insets;
                });

        edtIngredientName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        databaseHelper = new DatabaseHelper(this);

        String[] units = {"g", "kg", "ml", "l", "pieces"};

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        findViewById(R.id.btnSaveIngredient)
                .setOnClickListener(view -> saveIngredient());

        findViewById(R.id.btnCancel)
                .setOnClickListener(view -> finish());
    }

    private void saveIngredient() {
        String name = edtIngredientName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();

        if (name.isEmpty()) {
            edtIngredientName.setError("Enter an ingredient name");
            edtIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            edtQuantity.setError("Enter a quantity");
            edtQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText.replace(',', '.'));
        } catch (NumberFormatException exception) {
            edtQuantity.setError("Enter a valid number");
            edtQuantity.requestFocus();
            return;
        }

        if (Double.isNaN(quantity) || Double.isInfinite(quantity)
                || quantity <= 0) {
            edtQuantity.setError("Quantity must be greater than zero");
            edtQuantity.requestFocus();
            return;
        }

        if (unit.equals("pieces") && quantity != Math.floor(quantity)) {
            edtQuantity.setError("Enter a whole number for pieces");
            edtQuantity.requestFocus();
            return;
        }

        // Return to the pantry only after the database saves the item.
        try {
            long id = databaseHelper.addIngredient(name, quantity, unit);

            if (id == -1) {
                Toast.makeText(this, "Could not save ingredient",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Ingredient saved",
                    Toast.LENGTH_SHORT).show();
            finish();

        } catch (SQLiteException exception) {
            Toast.makeText(this, "Could not save ingredient. Try again.",
                    Toast.LENGTH_SHORT).show();
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