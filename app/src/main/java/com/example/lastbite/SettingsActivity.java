package com.example.lastbite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

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

        SharedPreferences preferences = getSharedPreferences(
                "last_bite_settings", MODE_PRIVATE
        );

        SwitchCompat pantryTips = findViewById(R.id.switchPantryTips);

        pantryTips.setChecked(
                preferences.getBoolean("show_pantry_tips", true)
        );

        pantryTips.setOnCheckedChangeListener((button, isChecked) -> {
            preferences.edit()
                    .putBoolean("show_pantry_tips", isChecked)
                    .apply();

            Toast.makeText(
                    SettingsActivity.this,
                    "Setting saved.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        findViewById(R.id.btnSettingsBack)
                .setOnClickListener(view -> finish());
    }
}