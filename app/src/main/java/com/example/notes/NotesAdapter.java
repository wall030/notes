package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private ArrayList<Note> notes;
    private DatabaseManager db;
    private Context context;

    public NotesAdapter(ArrayList<Note> notes, DatabaseManager db, Context context) {
        this.notes = notes;
        this.db = db;
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.timestamp.setText(note.getTimestampFromatted());

        Category category = db.getCategoryById(note.getCategoryId());
        holder.category.setText(category != null ? category.getName() : "No Category");

        // Klick-Logik für das Bearbeiten einer Notiz
        holder.itemView.setOnClickListener(v -> {
            Intent editIntent = new Intent(context, DetailPage.class);
            editIntent.putExtra("note_id", note.getId());
            context.startActivity(editIntent);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static void attachSwipeToDelete(RecyclerView recyclerView, NotesAdapter adapter, DatabaseManager db, Context context) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {

                    Note note = adapter.notes.get(position);
                    db.deleteNoteById(note.getId());
                    adapter.notes.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showCategoryManagementMenu(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Manage Categories");
        builder.setItems(new String[]{"Add New Category", "Delete Category"}, (dialog, which) -> {
            if (which == 0) {
                showAddCategoryDialog(position);
            } else {
                showDeleteCategoryDialog(position);
            }
        });
        builder.setOnDismissListener(d -> notifyItemChanged(position)); // reset view
        builder.show();
    }

    private void showAddCategoryDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add New Category");

        EditText input = new EditText(context);
        input.setHint("Enter Category Name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newCategory = input.getText().toString().trim();
            if (!newCategory.isEmpty()) {
                if (db.getCategoryIdByName(newCategory) != -1) {
                    Toast.makeText(context, "Category already exists", Toast.LENGTH_SHORT).show();
                } else {
                    db.insertCategory(newCategory);
                    Toast.makeText(context, "Category added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteCategoryDialog(int position) {
        List<Category> categories = db.getAllCategories();
        if (categories.isEmpty()) {
            Toast.makeText(context, "No categories to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] categoryNames = categories.stream().map(Category::getName).toArray(String[]::new);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Category");
        builder.setItems(categoryNames, (dialog, which) -> {
            int categoryId = categories.get(which).getId();
            if (db.safeDeleteCategoryById(categoryId)) {
                Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Category is in use and cannot be deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, timestamp, category;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            content = itemView.findViewById(R.id.card_content);
            timestamp = itemView.findViewById(R.id.note_timestamp);
            category = itemView.findViewById(R.id.note_category);
        }
    }
}
