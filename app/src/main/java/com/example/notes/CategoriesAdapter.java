package com.example.notes;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.persistence.DatabaseManager;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<String> categories;
    private final Context context;
    private final DatabaseManager db;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryName.setText(category);

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
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.delete_category_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        String message = context.getString(R.string.delete_category_message, category);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        messageTextView.setText(message);

        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button deleteButton = dialogView.findViewById(R.id.ok_button);

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            int categoryId = db.getCategoryIdByName(category);
            if (categoryId != -1) {
                if (db.safeDeleteCategoryById(categoryId)) {
                    categories.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Category is in use and cannot be deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}
