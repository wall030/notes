package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.notes.persistence.DatabaseManager;
import com.example.notes.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetailPage extends AppCompatActivity {

    private EditText titleInput, contentInput;
    private Spinner categorySpinner;
    private Button saveButton;
    private ImageView manageCategoriesIcon;

    final private DatabaseManager db = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Initialize views
        titleInput = findViewById(R.id.title_text);
        contentInput = findViewById(R.id.paragraph_text);
        saveButton = findViewById(R.id.save_button);
        categorySpinner = findViewById(R.id.category_spinner);
        manageCategoriesIcon = findViewById(R.id.manage_categories_icon);

        // Load categories from the database
        loadCategories();

        // Manage Categories icon click
        manageCategoriesIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DetailPage.this, CategoryManagementActivity.class);
            startActivity(intent);
        });

        // Check if we are editing an existing note
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("note_id")) {
            int noteId = intent.getIntExtra("note_id", -1);
            Note note = db.getNoteById(noteId);
            if (note != null) {
                titleInput.setText(note.getTitle());
                contentInput.setText(note.getContent());
                // Set the category in the spinner (this part needs to be adapted)
                // assuming you have category ID stored in the note
                setCategoryInSpinner(note.getCategoryId());
            }
        }

        // Save button functionality
        saveButton.setOnClickListener(view -> {
            String title = titleInput.getText().toString();
            String content = contentInput.getText().toString();
            String timestamp = DateUtil.getCurrentTimestamp();
            int selectedCategoryId = categorySpinner.getSelectedItemPosition(); // Get selected category ID

            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("content", content);
            resultIntent.putExtra("timestamp", timestamp);
            resultIntent.putExtra("categoryId", selectedCategoryId);

            if (intent != null && intent.hasExtra("note_id")) {
                int noteId = intent.getIntExtra("note_id", -1);
                db.updateNote(noteId, title, content, timestamp, selectedCategoryId);
                resultIntent.putExtra("note_id", noteId);
            } else {
                db.insertNote(timestamp, title, content, selectedCategoryId); // Add category ID to the insert method
            }

            setResult(RESULT_OK, resultIntent);
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

    private void loadCategories() {
        // Load categories from the database
        List<String> categoryNames = db.getAllCategories().stream().map(Category::getName).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setCategoryInSpinner(int categoryId) {
        // Set the selected category in the spinner (this logic needs to adapt according to your app structure)
        List<Category> categoriesList = db.getAllCategories();
        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).equals(categoryId)) {
                categorySpinner.setSelection(i);
                break;
            }
        }
    }
}
