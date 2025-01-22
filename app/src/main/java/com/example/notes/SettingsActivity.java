package com.example.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import android.os.Bundle;

public class SettingsActivity extends GlobalActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
        setTheme(R.style.PreferenceTheme);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            findPreference("dark_mode").setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isDarkMode = (boolean) newValue;
                SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("DarkMode", isDarkMode);
                editor.apply();

                AppCompatDelegate.setDefaultNightMode(isDarkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);

                return true;
            });

            findPreference("sort_by").setOnPreferenceChangeListener((preference, newValue) -> {
                String sortBy = (String) newValue;
                SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SortBy", sortBy);
                editor.apply();

                return true;
            });

            findPreference("manage_categories").setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(requireContext(), CategoryManagementActivity.class);
                startActivity(intent);
                return true;
            });

        }


    }
}
