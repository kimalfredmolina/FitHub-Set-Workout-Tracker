package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class Chest extends AppCompatActivity {

    private ListView lvExercises;
    private ArrayAdapter<String> adapter;
    private String[] exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chest);

        lvExercises = findViewById(R.id.Workout_list);

        // Get the selected muscle group from the intent
        String selectedMuscleGroup = getIntent().getStringExtra("selectedMuscleGroup");

        // Load exercises based on the muscle group selected
        loadExercisesForMuscleGroup(selectedMuscleGroup);

        // Set up the adapter and ListView to show the exercises
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exercises);
        lvExercises.setAdapter(adapter);

        // Set item click listener to handle exercise selection
        lvExercises.setOnItemClickListener((parent, view, position, id) -> {
            String selectedExercise = exercises[position];

            // Start WorkoutActivity to record the workout
            Intent intent = new Intent(Chest.this, AddExercise.class);
            intent.putExtra("selectedExercise", selectedExercise);
            startActivity(intent);
        });
    }

    private void loadExercisesForMuscleGroup(String muscleGroup) {
        // Load different exercises based on the selected muscle group
        switch (muscleGroup) {
            case "Chest":
                exercises = new String[]{"Bench Press", "Push Ups", "Chest Fly"};
                break;
            case "Back":
                exercises = new String[]{"Pull Ups", "Lat Pulldown", "Rows"};
                break;
            case "Abs":
                exercises = new String[]{"Crunches", "Leg Raises", "Plank"};
                break;
            case "Legs":
                exercises = new String[]{"Squats", "Lunges", "Leg Press"};
                break;
            case "Biceps":
                exercises = new String[]{"Bicep Curl", "Hammer Curl", "Chin Ups"};
                break;
            case "Triceps":
                exercises = new String[]{"Tricep Dips", "Tricep Pushdown", "Overhead Tricep Extension"};
                break;
            default:
                exercises = new String[]{};
        }
    }
}

