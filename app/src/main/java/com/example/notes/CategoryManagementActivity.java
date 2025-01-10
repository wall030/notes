package com.example.notes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryManagementActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private Button addCategoryButton;
    private CategoriesAdapter categoriesAdapter;
    private List<String> categoriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        addCategoryButton = findViewById(R.id.add_category_button);

        // Set up RecyclerView
        categoriesAdapter = new CategoriesAdapter(categoriesList, this);
        categoryRecyclerView.setAdapter(categoriesAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load categories
        loadCategories();

        // Add Category button
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadCategories() {
        DatabaseManager db = new DatabaseManager(this);
        categoriesList = db.getAllCategories().stream().map(Category::getName).collect(Collectors.toList());
        categoriesAdapter.updateCategories(categoriesList);
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newCategory = input.getText().toString().trim();
            if (!newCategory.isEmpty()) {
                DatabaseManager db = new DatabaseManager(this);
                if (categoriesList.contains(newCategory)) {
                    Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show();
                } else {
                    db.insertCategory(newCategory);
                    loadCategories();
                }
            } else {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
