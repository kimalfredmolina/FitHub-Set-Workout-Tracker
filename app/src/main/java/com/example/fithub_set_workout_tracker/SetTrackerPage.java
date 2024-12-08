package com.example.fithub_set_workout_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.sets_tracker.AddExercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SetTrackerPage extends AppCompatActivity {

    private FrameLayout fragmentContainer;
    private ScrollView dataLayout;
    private FloatingActionButton addExerciseButton;

    // Add TextViews to display the selected exercise data
    private TextView workoutNameTextView, dateTextView, timeTextView, notesTextView;

    private static final int REQUEST_CODE = 1;

    // To persist the exercise data
    private String workoutName;
    private String date;
    private String time;
    private String notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tracker_page);

        fragmentContainer = findViewById(R.id.fragment_container);
        dataLayout = findViewById(R.id.data_layout);
        addExerciseButton = findViewById(R.id.add_exercise);

        addExerciseButton.setOnClickListener(v -> {
            Intent intent = new Intent(SetTrackerPage.this, AddExercise.class);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

}
