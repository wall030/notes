package com.example.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.persistence.DatabaseManager;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<String> categories;
    private Context context;
    private DatabaseManager db;

    public CategoriesAdapter(List<String> categories, Context context, DatabaseManager db) {
        this.categories = categories;
        this.context = context;
        this.db = db;
    }

    public void updateCategories(List<String> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryName.setText(category);

        // Lange drücken, um die Kategorie zu löschen
        holder.itemView.setOnLongClickListener(v -> {
            showDeleteCategoryDialog(category, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private void showDeleteCategoryDialog(String category, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Category");
        builder.setMessage("Are you sure you want to delete the category \"" + category + "\"?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            int categoryId = db.getCategoryIdByName(category);
            if (categoryId != -1) {
                if (db.safeDeleteCategoryById(categoryId)) {
                    categories.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Category is in use and cannot be deleted", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add New Category");

        // Erstelle einen LinearLayout-Container für das EditText
        final EditText input = new EditText(context);
        input.setHint("Enter Category Name");
        input.setFocusable(true);
        input.setFocusableInTouchMode(true);
        input.requestFocus();

        // Setze das EditText in den Dialog
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newCategory = input.getText().toString().trim();
            if (!newCategory.isEmpty()) {
                if (categories.contains(newCategory)) {
                    Toast.makeText(context, "Category already exists", Toast.LENGTH_SHORT).show();
                } else {
                    db.insertCategory(newCategory);
                    updateCategories(db.getAllCategoriesAsStringList()); // Liste aktualisieren
                    Toast.makeText(context, "Category added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> input.requestFocus()); // Fokussiere das Eingabefeld
        dialog.show();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}