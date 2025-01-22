package com.example.notes;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notes.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
/*
public class EditNoteActivity extends AppCompatActivity {

    private DatabaseManager db;
    private EditText titleEditText, contentEditText;
    private Spinner categorySpinner;
    private Button saveButton;
    private int noteId = -1, selectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        db = new DatabaseManager(this);

        // Initialisiere UI-Elemente
        titleEditText = findViewById(R.id.edit_note_title);
        contentEditText = findViewById(R.id.edit_note_content);
        categorySpinner = findViewById(R.id.category_spinner);
        saveButton = findViewById(R.id.save_note_button);

        // Hole die Note-ID aus dem Intent
        noteId = getIntent().getIntExtra("note_id", -1);

        // Lade Notiz, falls vorhanden
        if (noteId != -1) {
            loadNoteDetails();
        }

        // Lade Kategorien in den Spinner
        loadCategories();

        // Speichern-Button konfigurieren
        saveButton.setOnClickListener(v -> saveNote());
    }

    private void loadNoteDetails() {
        Note note = db.getNoteById(noteId);
        if (note != null) {
            titleEditText.setText(note.getTitle());
            contentEditText.setText(note.getContent());
            selectedCategoryId = note.getCategoryId();
        } else {
            Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadCategories() {
        List<Category> categories = db.getAllCategories();
        if (categories.isEmpty()) {
            Toast.makeText(this, "No categories available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Spinner-Adapter erstellen
        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Setze die Kategorie des geladenen Notes
        if (noteId != -1) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == selectedCategoryId) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }

        // Überwache die Auswahl des Spinners
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedCategoryId = categories.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Keine Aktion erforderlich
            }
        });
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        long timestamp = System.currentTimeMillis();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (noteId == -1) {
            // Neue Notiz erstellen
            db.insertNote(title, content, timestamp, selectedCategoryId);
            Toast.makeText(this, "Note created", Toast.LENGTH_SHORT).show();
        } else {
            // Bestehende Notiz aktualisieren
            db.updateNote(noteId, title, content, timestamp, selectedCategoryId);
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
*/