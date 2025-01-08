package com.example.notes;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class GlobalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Lade den Dark Mode-Status vor dem Aufruf von super.onCreate()
        applyDarkMode();
        super.onCreate(savedInstanceState);
    }

    private void applyDarkMode() {
        // SharedPreferences für Dark Mode-Einstellung laden
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("DarkMode", false);

        // Dark Mode anwenden
        AppCompatDelegate.setDefaultNightMode(isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
