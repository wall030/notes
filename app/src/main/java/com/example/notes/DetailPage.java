package com.example.notes;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notes.persistence.DatabaseManager;
import com.example.notes.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class DetailPage extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Spinner categorySpinner;
    private Button saveButton;

    private DatabaseManager db;
    private int noteId = -1;
    private int selectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = new DatabaseManager(this);

        // UI-Elemente initialisieren
        titleEditText = findViewById(R.id.edit_note_title);
        contentEditText = findViewById(R.id.edit_note_content);
        categorySpinner = findViewById(R.id.category_spinner);
        saveButton = findViewById(R.id.save_note_button);

        // Notiz-ID aus Intent holen
        noteId = getIntent().getIntExtra("note_id", -1);

        // Notiz laden, falls vorhanden
        if (noteId != -1) {
            loadNoteDetails();
        }

        // Kategorien in Spinner laden
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
        }
    }

    private void loadCategories() {
        List<Category> categories = db.getAllCategories();
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        // Spinner-Adapter konfigurieren
        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(this, android.R.layout.simple_spinner_item, categories);
        categorySpinner.setAdapter(adapter);

        // Vorher ausgewählte Kategorie setzen
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == selectedCategoryId) {
                categorySpinner.setSelection(i);
                break;
            }
        }

        // Kategorieauswahl überwachen
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Aktualisiere die ausgewählte Kategorie-ID
                selectedCategoryId = ((Category) parent.getItemAtPosition(position)).getId();
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
        String timestamp = DateUtil.getCurrentTimestamp();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (noteId == -1) {
            // Neue Notiz speichern
            db.insertNote(title, content, timestamp, selectedCategoryId);
            Toast.makeText(this, "Note created", Toast.LENGTH_SHORT).show();
        } else {
            // Vorhandene Notiz aktualisieren
            db.updateNote(noteId, title, content, timestamp, selectedCategoryId);
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
