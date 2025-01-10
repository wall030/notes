package com.example.notes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notes.persistence.DatabaseManager;

public class EditNoteActivity extends AppCompatActivity {

    private DatabaseManager db;
    private EditText titleEditText, contentEditText;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        db = new DatabaseManager(this);

        // Initialisiere UI-Elemente
        titleEditText = findViewById(R.id.edit_note_title);
        contentEditText = findViewById(R.id.edit_note_content);
        Button saveButton = findViewById(R.id.save_note_button);

        // Hole die Note-ID aus dem Intent
        noteId = getIntent().getIntExtra("note_id", -1);

        // Lade die Note aus der Datenbank
        Note note = db.getNoteById(noteId);
        if (note != null) {
            titleEditText.setText(note.getTitle());
            contentEditText.setText(note.getContent());
        } else {
            Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Speichern-Button
        saveButton.setOnClickListener(v -> {
            String newTitle = titleEditText.getText().toString().trim();
            String newContent = contentEditText.getText().toString().trim();

            if (!newTitle.isEmpty() && !newContent.isEmpty()) {
                db.updateNote(noteId, newTitle, newContent, System.currentTimeMillis(), note.getCategoryId());
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
