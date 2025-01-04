package com.example.fithub_set_workout_tracker.main_pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.UpdateExercise;
import com.example.fithub_set_workout_tracker.sets_tracker.AddExercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SetTrackerPage extends Fragment {

    private LinearLayout dataLayout; // Should match your container layout inside ScrollView
    private FloatingActionButton addExerciseButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private static final int REQUEST_CODE = 1;
    private DatabaseReference databseref;
    private String isoDate;

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

    private String getMonthName(String monthNumber) {
        switch (monthNumber) {
            case "1": return "Jan";
            case "2": return "Feb";
            case "3": return "Mar";
            case "4": return "Apr";
            case "5": return "May";
            case "6": return "Jun";
            case "7": return "Jul";
            case "8": return "Aug";
            case "9": return "Sep";
            case "10": return "Oct";
            case "11": return "Nov";
            case "12": return "Dec";
            default: return "Invalid Month";
        }
    }

    private void loadWorkoutData() {
        String userId = mAuth.getCurrentUser().getUid();
        databaseReference.child("users")
                .child(userId)
                .child("workouts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                String date = dateSnapshot.getKey();

                                // Navigate through the "1" -> "2025" structure
                                DataSnapshot yearSnapshot = dateSnapshot.child("1").child("2025");
                                String program = yearSnapshot.child("program").getValue(String.class);

                                StringBuilder details = new StringBuilder();
                                DataSnapshot ex1Snapshot = yearSnapshot.child("exercises").child("ex1");

                                if (ex1Snapshot.exists()) {
                                    String exerciseName = ex1Snapshot.child("name").getValue(String.class);
                                    long setCount = ex1Snapshot.child("sets").getChildrenCount();

                                    if (exerciseName != null) {
                                        details.append(exerciseName)
                                                .append(" x")
                                                .append(setCount);
                                    }
                                }

                                addWorkoutCard(date, program != null ? program : "Workout",
                                        details.toString().trim());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load workouts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String formatDate(String dateStr) {
        try {
            // Check if dateStr is just a number
            if (dateStr.matches("\\d+")) {
                return "Day " + dateStr; // Simple format for numeric dates
            }
            // Handle ISO format (2025-01-03)
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                String monthName = getMonthName(String.valueOf(month));
                return monthName + " " + day;
            }
            // If no format matches, return original
            return dateStr;
        } catch (Exception e) {
            Log.e("SetTrackerPage", "Error formatting date: " + dateStr, e);
            return dateStr;
        }
    }
    private void addWorkoutCard(String date, String workoutTitle, String workoutDetails) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.workout_item, dataLayout, false);

        TextView dateText = cardView.findViewById(R.id.date_text); // Set date
        if (dateText != null) {
            dateText.setText(formatDate(date));
        }

        TextView workoutTitleText = cardView.findViewById(R.id.workout_title); // Set workout title
        if (workoutTitleText != null) {
            workoutTitleText.setText(workoutTitle);
        }

        TextView workoutDetailsText = cardView.findViewById(R.id.workout_details); // Set workout details
        if (workoutDetailsText != null) {
            workoutDetailsText.setText(workoutDetails);
        }

        // Make the card clickable
        cardView.setOnClickListener(v -> {
            String exerciseName = "";
            if (workoutDetails.contains(" x")) {
                exerciseName = workoutDetails.split(" x")[0];
            }

            Intent intent = new Intent(getContext(), UpdateExercise.class);
            intent.putExtra("date", date); // Pass the ISO date directly
            intent.putExtra("workoutTitle", exerciseName);
            intent.putExtra("workoutType", workoutTitle);

            Log.d("SetTrackerPage", "Sending to UpdateExercise - " +
                    "date: " + date +
                    ", workoutTitle: " + exerciseName +
                    ", workoutType: " + workoutTitle);

            startActivity(intent);
        });

        cardView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete Workout Data Card")
                    .setMessage("Are you sure you want to delete this workout?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        String userId = mAuth.getCurrentUser().getUid();

                        String formattedDate = isoDate;  //ISO format date (2025-01-03)

                        databseref = FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(userId)
                                .child("workouts")
                                .child(formattedDate);

                        Log.d("SetTrackerPage", "Deleting from path: " + databseref.toString());

                        databseref.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    // Remove the card from the UI
                                    dataLayout.removeView(cardView);
                                    Toast.makeText(getContext(), "Workout deleted successfully", Toast.LENGTH_SHORT).show();
                                    Log.d("SetTrackerPage", "Successfully deleted workout");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to delete workout: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("SetTrackerPage", "Failed to delete workout", e);
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();

            return true;
        });
        dataLayout.addView(cardView);
    }
}