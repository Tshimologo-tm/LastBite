package com.example.lastbite;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddIngredientActivity extends AppCompatActivity {

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

        Spinner spinnerUnit = findViewById(R.id.spinnerUnit);
        String[] units = {"g", "kg", "ml", "l", "pieces"};

        // Supply the unit choices to the dropdown.
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        // Close the form and return to the pantry.
        findViewById(R.id.btnCancel).setOnClickListener(view -> finish());
    }
}