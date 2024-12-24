package com.example.fithub_set_workout_tracker.main_pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.sets_tracker.AddExercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetTrackerPage extends Fragment {

    private LinearLayout dataLayout; // Should match your container layout inside ScrollView
    private FloatingActionButton addExerciseButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private static final int REQUEST_CODE = 1;

    public SetTrackerPage() {
    }

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_tracker_page, container, false);

        addExerciseButton = view.findViewById(R.id.add_exercise);
        dataLayout = view.findViewById(R.id.exercise_container); // Ensure this matches your XML ID for the container

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadWorkoutData();

        addExerciseButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddExercise.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        return view;
    }

    private void loadWorkoutData() {
        String userId = mAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(userId).child("workout")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                String date = dateSnapshot.getKey(); // Get date

                                for (DataSnapshot monthSnapshot : dateSnapshot.getChildren()) {
                                    for (DataSnapshot yearSnapshot : monthSnapshot.getChildren()) {
                                        String year = yearSnapshot.getKey();

                                        for (DataSnapshot workoutTypeSnapshot : yearSnapshot.getChildren()) {
                                            String workoutName = workoutTypeSnapshot.getKey(); // e.g., Abs

                                            StringBuilder details = new StringBuilder();

                                            for (DataSnapshot exerciseSnapshot : workoutTypeSnapshot.getChildren()) {
                                                String exerciseName = exerciseSnapshot.getKey(); // e.g., Crunches
                                                long setCount = exerciseSnapshot.child("sets").getChildrenCount(); // Count sets

                                                details.append(exerciseName)
                                                        .append(" x")
                                                        .append(setCount)
                                                        .append("\n");
                                            }

                                            addWorkoutCard(date, workoutName, details.toString().trim());
                                        }
                                    }
                                }
                            }
                        } else {
                            // Handle no data found
                            addWorkoutCard("No Data", "No Workouts", "Add some workouts to track!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }

    private void addWorkoutCard(String date, String workoutTitle, String workoutDetails) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.workout_item, dataLayout, false);

        // Set date
        TextView dateText = cardView.findViewById(R.id.date_text);
        if (dateText != null) {
            dateText.setText(formatDate(date));
        }

        // Set workout title
        TextView workoutTitleText = cardView.findViewById(R.id.workout_title);
        if (workoutTitleText != null) {
            workoutTitleText.setText(workoutTitle);
        }

        // Set workout details
        TextView workoutDetailsText = cardView.findViewById(R.id.workout_details);
        if (workoutDetailsText != null) {
            workoutDetailsText.setText(workoutDetails);
        }

        // Add the card to the container
        dataLayout.addView(cardView);
    }

    private String formatDate(String date) {
        // Example date formatting logic (adjust based on your date format)
        // You can use SimpleDateFormat for more advanced formatting
        return date.replace("/", "\n");
    }
}