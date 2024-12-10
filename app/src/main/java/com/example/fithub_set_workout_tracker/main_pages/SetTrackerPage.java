package com.example.fithub_set_workout_tracker.main_pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.sets_tracker.AddExercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SetTrackerPage extends Fragment {

    private ScrollView dataLayout;
    private FloatingActionButton addExerciseButton;

    private TextView workoutNameTextView, dateTextView, timeTextView, notesTextView;

    private static final int REQUEST_CODE = 1;

    public SetTrackerPage() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_tracker_page, container, false);


        addExerciseButton = view.findViewById(R.id.add_exercise);

        addExerciseButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddExercise.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        return view;
    }
}


