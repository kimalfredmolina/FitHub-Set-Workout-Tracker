package com.example.fithub_set_workout_tracker.main_pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.sets_tracker.AddExercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SetTrackerPage extends Fragment {

    private ScrollView dataLayout;
    private FloatingActionButton addExerciseButton;
    private LinearLayout exerciseContainer;

    public SetTrackerPage() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_tracker_page, container, false);

        dataLayout = view.findViewById(R.id.scroll_view_data);
        addExerciseButton = view.findViewById(R.id.add_exercise);
        exerciseContainer = view.findViewById(R.id.exercise_container);

        fetchExerciseData();

        addExerciseButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddExercise.class);
            startActivity(intent); // Directly start the AddExercise activity
        });

        return view;
    }

    private void fetchExerciseData() {

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("workouts");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                exerciseContainer.removeAllViews();

                for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    Map<String, Object> workoutData = (HashMap<String, Object>) workoutSnapshot.getValue();

                    if (workoutData != null) {
                        String workoutName = (String) workoutData.get("program");
                        String date = (String) workoutData.get("date");
                        String startTime = (String) workoutData.get("startTime");
                        String endTime = (String) workoutData.get("endTime");
                        String notes = (String) workoutData.get("notes");

                        View workoutView = LayoutInflater.from(getContext()).inflate(R.layout.item_workout, exerciseContainer, false);

                        TextView workoutNameTextView = workoutView.findViewById(R.id.workout_name);
                        TextView dateTextView = workoutView.findViewById(R.id.workout_date);
                        TextView timeTextView = workoutView.findViewById(R.id.workout_time);
                        TextView notesTextView = workoutView.findViewById(R.id.workout_notes);

                        workoutNameTextView.setText(workoutName);
                        dateTextView.setText(date);
                        timeTextView.setText(startTime + " - " + endTime);
                        notesTextView.setText(notes);

                        exerciseContainer.addView(workoutView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                error.toException().printStackTrace();
            }
        });
    }
}
