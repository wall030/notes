package com.example.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.persistence.DatabaseManager;
import com.example.notes.persistence.Note;

import java.util.ArrayList;

public class MainPage extends GlobalActivity {

    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayList<Note> filteredNotes = new ArrayList<>();
    private NotesAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseManager db;
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private String TAG = "MainPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);

        db = new DatabaseManager(this);

        recyclerView = findViewById(R.id.card_container);
        adapter = new NotesAdapter(filteredNotes, db, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NotesAdapter.attachSwipeToDelete(recyclerView, adapter, db, this);

        findViewById(R.id.fab).setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, DetailPage.class);
            startActivity(intent);
        });

        findViewById(R.id.menu_icon).setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, SettingsActivity.class);
            startActivity(intent);
        });

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

        preferenceChangeListener = (sharedPreferences, key) -> {
            if ("sort_by".equals(key)) {
                loadNotes(sharedPreferences.getString("sort_by", "timestamp"));
            }
        };

        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadNotes(preferences.getString("sort_by", "timestamp"));
    }

    private void loadNotes(String sortBy) {
        try {
            notes.clear();
            notes.addAll(db.getAllNotes(sortBy));
            filteredNotes.clear();
            filteredNotes.addAll(notes);

            if (notes.isEmpty()) {
                Log.i(TAG, "No stored notes");
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Error while getting notes");
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
