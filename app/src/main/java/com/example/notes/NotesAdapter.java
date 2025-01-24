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

import com.example.notes.persistence.Category;
import com.example.notes.persistence.DatabaseManager;
import com.example.notes.persistence.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final ArrayList<Note> notes;
    private final DatabaseManager db;
    private final Context context;

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
                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
