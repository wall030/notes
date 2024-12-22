package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Button;

import com.example.notes.persistence.DatabaseManager;
import com.example.notes.util.DateUtil;

public class DetailPage extends AppCompatActivity {

    private EditText titleInput, contentInput;
    private Button saveButton;

    final private DatabaseManager db = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        titleInput = findViewById(R.id.title_text);
        contentInput = findViewById(R.id.paragraph_text);
        saveButton = findViewById(R.id.save_button);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("note_id")) {
            int noteId = intent.getIntExtra("note_id", -1);

            Note note = db.getNoteById(noteId);
            if (note != null) {
                titleInput.setText(note.getTitle());
                contentInput.setText(note.getContent());
            }
        }

        saveButton.setOnClickListener(view -> {
            String title = titleInput.getText().toString();
            String content = contentInput.getText().toString();
            String timestamp = DateUtil.getCurrentTimestamp();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("content", content);
            resultIntent.putExtra("timestamp", timestamp);

            if (intent != null && intent.hasExtra("note_id")) {
                int noteId = intent.getIntExtra("note_id", -1);
                db.updateNote(noteId, title, content, timestamp);
                resultIntent.putExtra("note_id", noteId);
            } else {
                db.insertNote(timestamp, title, content, 1);
            }

            setResult(RESULT_OK, resultIntent);
            finish();
        });

        findViewById(R.id.delete_24).setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Back arrow clicked
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
