package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.UpdateExercise;

public class Update_Exercises extends AppCompatActivity {

    private ListView lvExercises;
    private ArrayAdapter<String> adapter;
    private String[] exercises;
    private String selectedMuscleGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_exercises);

        lvExercises = findViewById(R.id.update_workout_list);

        selectedMuscleGroup = getIntent().getStringExtra("selectedMuscleGroup");

        if (selectedMuscleGroup == null) {
            selectedMuscleGroup = "Unknown";
            finish();
        }

        loadExercisesForMuscleGroup(selectedMuscleGroup);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exercises);
        lvExercises.setAdapter(adapter);

        lvExercises.setOnItemClickListener((parent, view, position, id) -> {
            String selectedExercise = exercises[position];

            Intent intent = new Intent(Update_Exercises.this, UpdateExercise.class);
            intent.putExtra("selectedExercise", selectedExercise);
            intent.putExtra("selectedMuscleGroup", selectedMuscleGroup);
            startActivity(intent);
        });
    }


    private void loadExercisesForMuscleGroup(String muscleGroup) {
        switch (muscleGroup) {
            case "Chest":
                exercises = new String[]{"Bench Press", "Cable Crossover", "Dumbbell Press", "Dumbbell Flies", "Inclined Dumbbell Press", "Inclined Bench Press", "Chest Fly"};
                break;
            case "Back":
                exercises = new String[]{"Pull Ups", "Lat Pulldown", "Rows", "Chin Up", "Barbell Row", "Cable Row",  "Deadlift", "Dumbbell Row",  "Pull Downs"};
                break;
            case "Abs":
                exercises = new String[]{"Crunches", "Leg Raises", "Plank"};
                break;
            case "Legs":
                exercises = new String[]{"Squats", "Lunges", "Leg Press", "Calf Raises", "Front Squat", "Leg Curl", "Leg Extension"};
                break;
            case "Biceps":
                exercises = new String[]{"Bicep Curl", "Hammer Curl", "Chin Ups"};
                break;
            case "Triceps":
                exercises = new String[]{"Tricep Dips", "Tricep Pushdown", "Overhead Tricep Extension", "Skull Crush"};
                break;
            case "Shoulders":
                exercises = new String[]{"Overhead Press", "Lateral Raise", "Front Raise", "Reverse Fly", "Arnold Press", "Face Pull"};
                break;
            default:
                exercises = new String[]{"No exercises available"};
        }
    }
}