package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.notes.R;

public class CRUD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        // Get the reference to the ImageView
        ImageView backArrow = findViewById(R.id.back_arrow_24);

        // Set the OnClickListener for the back arrow image
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainUI activity
                Intent intent = new Intent(CRUD.this, MainUI.class);
                startActivity(intent);

                // Optionally, you can also call finish() to remove the CRUD activity from the back stack
                finish();
            }
        });
    }
}