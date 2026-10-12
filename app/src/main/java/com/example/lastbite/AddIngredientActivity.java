package com.example.lastbite;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText edtIngredientName;
    private EditText edtQuantity;
    private Spinner spinnerUnit;
    private DatabaseHelper databaseHelper;
    private long ingredientId = -1;

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

        ingredientId = getIntent().getLongExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            Button saveButton = findViewById(R.id.btnSaveIngredient);
            saveButton.setText("Save Changes");

            android.widget.TextView formTitle =
                    findViewById(R.id.txtFormTitle);
            formTitle.setText("Edit Ingredient");

            if (savedInstanceState == null) {
                loadIngredient(unitAdapter);
            }
        }

        findViewById(R.id.btnSaveIngredient)
                .setOnClickListener(view -> saveIngredient());

        findViewById(R.id.btnCancel)
                .setOnClickListener(view -> finish());
    }

    private void loadIngredient(ArrayAdapter<String> unitAdapter) {
        // Read the existing row using the ID passed from the pantry.
        try (Cursor cursor = databaseHelper.getIngredient(ingredientId)) {
            if (!cursor.moveToFirst()) {
                Toast.makeText(this, "Ingredient no longer exists",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("name"));
            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("quantity"));
            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow("unit"));

            edtIngredientName.setText(name);
            edtQuantity.setText(BigDecimal.valueOf(quantity)
                    .stripTrailingZeros().toPlainString());
            spinnerUnit.setSelection(unitAdapter.getPosition(unit));

        } catch (SQLiteException exception) {
            Toast.makeText(this, "Could not load ingredient",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
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

        try {
            boolean saved;

            // Update an existing row or insert a new ingredient.
            if (ingredientId == -1) {
                saved = databaseHelper.addIngredient(
                        name, quantity, unit) != -1;
            } else {
                saved = databaseHelper.updateIngredient(
                        ingredientId, name, quantity, unit) == 1;
            }

            if (!saved) {
                Toast.makeText(this, "Could not save ingredient",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String message = ingredientId == -1
                    ? "Ingredient saved" : "Ingredient updated";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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