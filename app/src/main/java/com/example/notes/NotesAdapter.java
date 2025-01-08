package com.example.notes;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.persistence.DatabaseManager;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private ArrayList<Note> notes;
    private NoteActionCallback callback;

    public NotesAdapter(ArrayList<Note> notes) {
        this.notes = notes;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailPage.class);
            intent.putExtra("note_id", note.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);

        if (notes.isEmpty()) {
            System.out.println("Keine Notizen mehr vorhanden.");
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, timestamp;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            content = itemView.findViewById(R.id.card_content);
            timestamp = itemView.findViewById(R.id.note_timestamp);
        }
    }

    public static void attachSwipeToDelete(RecyclerView recyclerView, NotesAdapter adapter, DatabaseManager db) {
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
                        if (adapter.callback != null) {
                            adapter.callback.onNoteDeleted(note);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        adapter.notifyItemChanged(position); // Wiederherstellen bei Fehler
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                Paint paint = new Paint();
                paint.setColor(Color.RED);

                // Hintergrund zeichnen
                c.drawRoundRect((float) viewHolder.itemView.getRight() + dX, (float) viewHolder.itemView.getTop(),
                        (float) viewHolder.itemView.getRight(), (float) viewHolder.itemView.getBottom(),16F, 16F, paint);

                // Icon zeichnen
                Drawable deleteIcon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.delete_24);
                int iconMargin = (viewHolder.itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconLeft = viewHolder.itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = viewHolder.itemView.getRight() - iconMargin;
                int iconTop = viewHolder.itemView.getTop() + iconMargin;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public interface NoteActionCallback {
        void onNoteDeleted(Note note);
    }
}
