package com.example.notes.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;
import com.example.notes.persistence.Category;
import com.example.notes.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryManagementActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private Button addCategoryButton;
    private CategoriesAdapter categoriesAdapter;
    private List<String> categoriesList = new ArrayList<>();
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);
        db = new DatabaseManager(this);

        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        addCategoryButton = findViewById(R.id.add_category_button);

        categoriesAdapter = new CategoriesAdapter(categoriesList, this, db);
        categoryRecyclerView.setAdapter(categoriesAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCategories();

        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private void loadCategories() {
        categoriesList = db.getAllCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        categoriesAdapter.updateCategories(categoriesList);
    }

    private void showAddCategoryDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.add_category_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        okButton.setOnClickListener(v -> {
            EditText input = dialogView.findViewById(R.id.dialog_input);
            String newCategory = input.getText().toString().trim();
            if (newCategory.isEmpty()) {
                input.setError("Category name cannot be empty");
                return;
            }

            if (categoriesList.contains(newCategory)) {
                Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show();
            } else {
                db.insertCategory(newCategory);
                loadCategories();
                Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
