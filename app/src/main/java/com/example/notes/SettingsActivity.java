package com.example.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends GlobalActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // Handle the Dark Mode switch
            findPreference("dark_mode").setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isDarkMode = (boolean) newValue;
                SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("DarkMode", isDarkMode);
                editor.apply();

                AppCompatDelegate.setDefaultNightMode(isDarkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);

                return true;
            });

            // Handle the "Sort Notes By" preference
            findPreference("sort_by").setOnPreferenceClickListener(preference -> {
                showSortByDialog();
                return true;
            });
        }

        private void showSortByDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.sort_by_dialog, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();

            // Set up the RadioGroup listener
            RadioGroup sortOptions = dialogView.findViewById(R.id.sort_notes_options);
            sortOptions.setOnCheckedChangeListener((group, checkedId) -> {
                String sortBy = null;
                if (checkedId == R.id.sort_by_timestamp) {
                    sortBy = "timestamp";
                } else if (checkedId == R.id.sort_by_title) {
                    sortBy = "title";
                } else if (checkedId == R.id.sort_by_category) {
                    sortBy = "category";
                }

                if (sortBy != null) {
                    // Capitalize the first letter
                    String formattedSortBy = sortBy.substring(0, 1).toUpperCase() + sortBy.substring(1);

                    // Save to SharedPreferences
                    SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("SortBy", formattedSortBy);
                    editor.apply();

                    // Update the summary text
                    findPreference("sort_by").setSummary(formattedSortBy);

                    dialog.dismiss();
                }
            });

            // Set up the Cancel button
            dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }
    }
}
