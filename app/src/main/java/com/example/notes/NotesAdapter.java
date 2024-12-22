package com.example.notes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.DetailPage;
import com.example.notes.Note;

import java.util.ArrayList;
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private ArrayList<Note> notes;

    public NotesAdapter(ArrayList<Note> notes) {
        this.notes = notes;
    }

    // Method to update the dataset and notify the adapter
    public void updateNotes(ArrayList<Note> updatedNotes) {
        this.notes.clear();
        this.notes.addAll(updatedNotes);
        notifyDataSetChanged(); // Notify RecyclerView to refresh
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

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, timestamp;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            content = itemView.findViewById(R.id.card_content);
            timestamp = itemView.findViewById(R.id.note_timestamp);
        }
    }
}
