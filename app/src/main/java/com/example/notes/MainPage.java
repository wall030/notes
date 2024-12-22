package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notes.persistence.DatabaseManager;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayAdapter<Note> adapter;
    private ListView listView;
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseManager(this);
        listView = findViewById(R.id.card_container);
        listView.setOnItemClickListener(this);

        // Button to add new notes
        findViewById(R.id.fab).setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, DetailPage.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Retrieve notes from the database
        notes.clear();
        notes.addAll(db.getAllNotes());

        // Create ArrayAdapter and pass the layout for CardView item
        adapter = new ArrayAdapter<>(this, R.layout.note_card, notes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_card, parent, false);
                }

                // Get the current note item
                Note note = getItem(position);

                // Find the views inside the CardView layout
                TextView titleView = convertView.findViewById(R.id.card_title);
                TextView contentView = convertView.findViewById(R.id.card_content);
                TextView timestampView = convertView.findViewById(R.id.note_timestamp);

                // Bind the note data to the views
                if (note != null) {
                    titleView.setText(note.getTitle());
                    contentView.setText(note.getContent());
                    timestampView.setText(note.getTimestampFromatted());
                }

                return convertView;
            }
        };

        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailPage.class);
        Note selectedNote = notes.get(position);
        intent.putExtra("note_id", selectedNote.getId());
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data != null) {
                notes.clear();
                notes.addAll(db.getAllNotes());
            }
        }
    }
}
