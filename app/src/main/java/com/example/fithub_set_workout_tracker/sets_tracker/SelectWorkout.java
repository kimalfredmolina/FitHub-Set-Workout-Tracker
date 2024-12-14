package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class SelectWorkout extends AppCompatActivity {

    private ListView lvMuscleGroups;
    private ArrayAdapter<String> adapter;
    private String[] muscleGroups = {"Chest", "Back", "Abs", "Legs", "Biceps", "Triceps"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_workout);

        lvMuscleGroups = findViewById(R.id.Workout_list);

        // Set up the adapter and ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, muscleGroups);
        lvMuscleGroups.setAdapter(adapter);

        // Set item click listener to handle muscle group selection
        lvMuscleGroups.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMuscleGroup = muscleGroups[position];

            // Start ExerciseListActivity and pass the selected muscle group
            Intent intent = new Intent(SelectWorkout.this, Chest.class);
            intent.putExtra("selectedMuscleGroup", selectedMuscleGroup);
            startActivity(intent);
        });
    }
}
