package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class SelectWorkout extends AppCompatActivity {

    private final String[] workouts = {
            "Abs", "Back", "Biceps", "Chest", "Legs", "Shoulders", "Triceps"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_workout);

        ListView workoutList = findViewById(R.id.Workout_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, workouts);

        workoutList.setAdapter(adapter);

        workoutList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedWorkout = workouts[position];
            // Navigate to the corresponding workout activity (e.g., Abs)
            Intent intent = new Intent(SelectWorkout.this, getActivityClass(selectedWorkout));
            startActivityForResult(intent, 1); // Open the selected workout activity
        });
    }

    private Class<?> getActivityClass(String workout) {
        switch (workout) {
            case "Abs":
                return Abs.class;
            case "Back":
                return Back.class;
            case "Biceps":
                return Biceps.class;
            case "Chest":
                return Chest.class;
            case "Legs":
                return Legs.class;
            case "Shoulders":
                return Shoulders.class;
            case "Triceps":
                return Triceps.class;
            default:
                return null;
        }
    }
}