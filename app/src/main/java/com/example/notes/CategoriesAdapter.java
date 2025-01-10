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

    public CategoriesAdapter(List<String> categories, Context context) {
        this.categories = categories;
        this.context = context;
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

        holder.itemView.setOnLongClickListener(v -> {
            showEditDeleteDialog(category, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private void showEditDeleteDialog(String category, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit or Delete Category");

        builder.setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
            DatabaseManager db = new DatabaseManager(context);
            if (which == 0) {
                showEditCategoryDialog(category, position);
            } else if (which == 1) {
                // Hole die ID der Kategorie und lösche sie
                int categoryId = db.getAllCategories().stream()
                        .filter(cat -> cat.getName().equals(category))
                        .findFirst()
                        .map(Category::getId)
                        .orElse(-1);

                if (categoryId != -1) {
                    db.deleteCategoryById(categoryId);
                    categories.remove(position);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    private void showEditCategoryDialog(String oldCategory, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Category");

        EditText input = new EditText(context);
        input.setText(oldCategory);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newCategory = input.getText().toString().trim();
            if (!newCategory.isEmpty()) {
                DatabaseManager db = new DatabaseManager(context);

                // Hole die ID der Kategorie und aktualisiere sie
                int categoryId = db.getAllCategories().stream()
                        .filter(cat -> cat.getName().equals(oldCategory))
                        .findFirst()
                        .map(Category::getId)
                        .orElse(-1);

                if (categoryId != -1) {
                    db.updateCategory(categoryId, newCategory);
                    categories.set(position, newCategory);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Error updating category", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}
