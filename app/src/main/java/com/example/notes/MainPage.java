package com.example.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.persistence.DatabaseManager;

import java.util.ArrayList;

public class MainPage extends GlobalActivity {

    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayList<Note> filteredNotes = new ArrayList<>();
    private NotesAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseManager db;
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);

        db = new DatabaseManager(this);

        // RecyclerView einrichten
        recyclerView = findViewById(R.id.card_container);
        adapter = new NotesAdapter(filteredNotes, db, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Swipe-to-Delete aktivieren
        NotesAdapter.attachSwipeToDelete(recyclerView, adapter, db, this);

        // Floating Action Button
        findViewById(R.id.fab).setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, DetailPage.class);
            startActivity(intent);
        });

        // Menü-Button für Einstellungen
        findViewById(R.id.menu_icon).setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, SettingsActivity.class);
            startActivity(intent);
        });

        // SearchBar einrichten
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterNotes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes(newText);
                return false;
            }
        });

        // Set up preference change listener
        preferenceChangeListener = (sharedPreferences, key) -> {
            if ("sort_by".equals(key)) {
                loadNotes(sharedPreferences.getString("sort_by", "timestamp"));
            }
        };

        // Register the listener
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadNotes(preferences.getString("sort_by", "timestamp"));
    }

    private void loadNotes(String sortBy) {
        try {
            // Lade alle Notizen
            notes.clear();
            notes.addAll(db.getAllNotes(sortBy));
            filteredNotes.clear();
            filteredNotes.addAll(notes);

            // Überprüfen, ob Notizen vorhanden sind
            if (notes.isEmpty()) {
                // Optional: Zeige eine Meldung oder einen Platzhalter
                System.out.println("Keine Notizen vorhanden.");
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            // Optional: Zeige eine Fehlermeldung
            System.out.println("Fehler beim Laden der Notizen: " + e.getMessage());
        }
    }

    private void filterNotes(String query) {
        filteredNotes.clear();
        if (TextUtils.isEmpty(query)) {
            filteredNotes.addAll(notes);
        } else {
            for (Note note : notes) {
                if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        note.getContent().toLowerCase().contains(query.toLowerCase())) {
                    filteredNotes.add(note);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preferences != null && preferenceChangeListener != null) {
            preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }
}
