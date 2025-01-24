package com.example.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.notes.category.CategoryManagementActivity;

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

        private static final String SORT_BY_KEY = "sort_by";

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

            findPreference("manage_categories").setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(requireContext(), CategoryManagementActivity.class);
                startActivity(intent);
                return true;
            });

            Preference sortByPreference = findPreference(SORT_BY_KEY);
            sortByPreference.setOnPreferenceClickListener(preference -> {
                showSortByDialog();
                return true;
            });

            SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
            String currentSortBy = preferences.getString(SORT_BY_KEY, "timestamp");
            sortByPreference.setSummary(format(currentSortBy));
        }

        private void showSortByDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.sort_by_dialog, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();

            SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
            String currentSortBy = preferences.getString("sort_by", "timestamp");

            // RadioGroup setup
            RadioGroup sortOptions = dialogView.findViewById(R.id.sort_notes_options);
            if ("timestamp".equals(currentSortBy)) {
                sortOptions.check(R.id.sort_by_timestamp);
            } else if ("title".equals(currentSortBy)) {
                sortOptions.check(R.id.sort_by_title);
            } else if ("category".equals(currentSortBy)) {
                sortOptions.check(R.id.sort_by_category);
            }

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
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("sort_by", sortBy);
                    editor.apply();

                    findPreference("sort_by").setSummary(format(sortBy));

                    dialog.dismiss();
                }
            });

            dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }

        private String format(String input) {
            if (input == null || input.isEmpty()) return input;
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }
    }
}
