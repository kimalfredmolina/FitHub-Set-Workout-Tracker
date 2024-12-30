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

public class Update_SelectWorkout extends AppCompatActivity {

    private ListView lvMuscleGroups;
    private ArrayAdapter<String> adapter;
    private String[] muscleGroups = {"Chest", "Back", "Abs", "Legs", "Biceps", "Triceps"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_select_workout);

        lvMuscleGroups = findViewById(R.id.update_work_list);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, muscleGroups);
        lvMuscleGroups.setAdapter(adapter);

        lvMuscleGroups.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMuscleGroup = muscleGroups[position];
            Intent intent = new Intent(Update_SelectWorkout.this, Update_Exercises.class);
            intent.putExtra("selectedMuscleGroup", selectedMuscleGroup); // Pass selected muscle group
            startActivity(intent);
        });
    }
}