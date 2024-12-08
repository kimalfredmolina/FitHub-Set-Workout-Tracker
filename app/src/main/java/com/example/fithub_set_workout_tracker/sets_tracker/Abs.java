package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class Abs extends AppCompatActivity {

    private final String[] abs = {"Crunches", "Leg Raises"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abs);

        ListView listView = findViewById(R.id.Workout_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, abs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedExercise = abs[position];

            // Passing the selected exercise back to the AddExercise activity
            Intent resultIntent = new Intent(Abs.this, AddExercise.class);
            resultIntent.putExtra("selectedExercise", selectedExercise);

            // Start AddExercise activity
            startActivity(resultIntent);
        });
    }
}
