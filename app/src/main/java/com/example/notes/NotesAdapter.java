package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private ArrayList<Note> notes;
    private NoteActionCallback callback;
    private DatabaseManager db;
    private Context context;

    public NotesAdapter(ArrayList<Note> notes, DatabaseManager db, Context context) {
        this.notes = notes;
        this.db = db;
        this.context = context;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNoteActionCallback(NoteActionCallback callback) {
        this.callback = callback;
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
            Intent editIntent = new Intent(context, EditNoteActivity.class);
            editIntent.putExtra("note_id", note.getId());
            context.startActivity(editIntent);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void removeItem(int position) {
        Note removedNote = notes.get(position);
        notes.remove(position);
        notifyItemRemoved(position);
        if (callback != null) {
            callback.onNoteDeleted(removedNote); // Callback aufrufen
        }
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

    public static void attachSwipeToDelete(RecyclerView recyclerView, NotesAdapter adapter, DatabaseManager db, Context context) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = adapter.getNotes().get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Löschen
                    try {
                        db.deleteNoteById(note.getId());
                        adapter.removeItem(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                        adapter.notifyItemChanged(position); // Wiederherstellen bei Fehler
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Kategorien verwalten
                    adapter.showCategoryManagementDialog(note, position);
                }

                // Ansicht immer zurücksetzen
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                Paint paint = new Paint();
                if (dX > 0) {
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setColor(Color.RED);
                }

                c.drawRoundRect((float) viewHolder.itemView.getLeft(), (float) viewHolder.itemView.getTop(),
                        (float) viewHolder.itemView.getLeft() + dX, (float) viewHolder.itemView.getBottom(), 16F, 16F, paint);

                Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(),
                        dX > 0 ? R.drawable.ic_menu : R.drawable.delete_24);
                int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconLeft = dX > 0 ? viewHolder.itemView.getLeft() + iconMargin : viewHolder.itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = dX > 0 ? iconLeft + icon.getIntrinsicWidth() : viewHolder.itemView.getRight() - iconMargin;
                int iconTop = viewHolder.itemView.getTop() + iconMargin;
                int iconBottom = iconTop + icon.getIntrinsicHeight();
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                icon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showCategoryManagementDialog(Note note, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Manage Categories");

        String[] options = {"Assign Category", "Remove Category", "Add New Category"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Kategorie zuweisen
                List<Category> categories = db.getAllCategories();
                CharSequence[] categoryNames = categories.stream()
                        .map(Category::getName)
                        .toArray(CharSequence[]::new);

                AlertDialog.Builder categoryDialog = new AlertDialog.Builder(context);
                categoryDialog.setTitle("Select a Category");
                categoryDialog.setItems(categoryNames, (dialog1, categoryPosition) -> {
                    int categoryId = categories.get(categoryPosition).getId();
                    note.setCategoryId(categoryId);
                    db.updateNote(note.getId(), note.getTitle(), note.getContent(), note.getTimestamp(), categoryId);

                    notifyItemChangedWithCategoryUpdate(position, note);
                });
                categoryDialog.setOnDismissListener(d -> notifyItemChanged(position));
                categoryDialog.show();

            } else if (which == 1) { // Kategorie entfernen
                note.setCategoryId(-1);
                db.updateNote(note.getId(), note.getTitle(), note.getContent(), note.getTimestamp(), -1);

                notifyItemChangedWithCategoryUpdate(position, note);

            } else if (which == 2) { // Neue Kategorie hinzufügen
                AlertDialog.Builder addCategoryDialog = new AlertDialog.Builder(context);
                addCategoryDialog.setTitle("Add New Category");

                EditText input = new EditText(context);
                addCategoryDialog.setView(input);

                addCategoryDialog.setPositiveButton("Add", (dialog1, which1) -> {
                    String newCategory = input.getText().toString().trim();
                    if (!newCategory.isEmpty()) {
                        db.insertCategory(newCategory);
                    }
                });

                addCategoryDialog.setNegativeButton("Cancel", null);
                addCategoryDialog.setOnDismissListener(d -> notifyItemChanged(position));
                addCategoryDialog.show();
            }
        });

        builder.setOnDismissListener(d -> notifyItemChanged(position));
        builder.show();
    }

    private void notifyItemChangedWithCategoryUpdate(int position, Note note) {
        Category updatedCategory = db.getCategoryById(note.getCategoryId());
        if (updatedCategory != null) {
            note.setCategoryId(updatedCategory.getId());
        } else {
            note.setCategoryId(-1);
        }

        notes.set(position, note);
        notifyItemChanged(position);
    }

    public interface NoteActionCallback {
        void onNoteDeleted(Note note);
    }
}
