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
        databaseReference.child("Users").child(userId).child("workout")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                String date = dateSnapshot.getKey();

                                for (DataSnapshot monthSnapshot : dateSnapshot.getChildren()) {
                                    String monthNumber = monthSnapshot.getKey();
                                    String monthName = getMonthName(monthNumber);

                                    for (DataSnapshot yearSnapshot : monthSnapshot.getChildren()) {
                                        String year = yearSnapshot.getKey();

                                        for (DataSnapshot workoutTypeSnapshot : yearSnapshot.getChildren()) {
                                            String workoutName = workoutTypeSnapshot.getKey();

                                            StringBuilder details = new StringBuilder();

                                            for (DataSnapshot exerciseSnapshot : workoutTypeSnapshot.getChildren()) {
                                                String exerciseName = exerciseSnapshot.getKey();
                                                long setCount = exerciseSnapshot.child("sets").getChildrenCount();

                                                details.append(exerciseName)
                                                        .append(" x")
                                                        .append(setCount)
                                                        .append("\n");
                                            }

                                            addWorkoutCard(monthName + " " + date, workoutName, details.toString().trim());
                                        }
                                    }
                                }
                            }
                        } else {
                            addWorkoutCard("No Data", "No Workouts", "Add some workouts to track!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
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

        // Extract month, day, year, and workout type
        String[] dateParts = date.split(" "); // Example date: "Dec 25"
        if (dateParts.length != 2) {
            Log.e("SetTrackerPage", "Invalid date format: " + date);
            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert month name to number and extract the day
        String monthStr = dateParts[0]; // "Dec"
        String day = dateParts[1]; // "25"
        String month = String.valueOf(getMonthNumber(monthStr)); // Convert "Dec" to "12"
        String year = "2025";

        // Extract the workout type (e.g., "Leg Press" from "Leg Press x1")
        String workoutType = workoutDetails.split(" x")[0];

        // Make the card clickable
        cardView.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser().getUid();

            Intent intent = new Intent(getContext(), UpdateExercise.class);
            intent.putExtra("date", day + "/" + month + "/" + year); // Example: "25/12/2025"
            intent.putExtra("workoutTitle", workoutType); // Example: "Leg Press"
            intent.putExtra("workoutType", workoutTitle); // Example: "Legs"

            Log.d("SetTrackerPage", "Sending to UpdateExercise - " +
                    "date: " + (day + "/" + month + "/" + year) +
                    ", workoutTitle: " + workoutType +
                    ", workoutType: " + workoutTitle);

            startActivity(intent);
        });

        // Long press to delete the card
        cardView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete Workout Data Card")
                    .setMessage("Are you sure you want to delete this workout?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        String userId = mAuth.getCurrentUser().getUid();

                        // Format the date to match your save format
                        String formattedDate = day + "/" + month + "/" + year;  // This matches your save format

                        // Database reference using the same structure as your save code
                        databseref = FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(userId)
                                .child("workout")
                                .child(formattedDate)    // Use the full date as one child
                                .child(workoutTitle)     // This is your muscle group (e.g., "Legs")
                                .child(workoutType);     // This is your exercise (e.g., "Leg Press")

                        // Add logging to debug the path
                        Log.d("SetTrackerPage", "Deleting from path: " + databseref.toString());

                        // Remove the node from the database
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
        // Add the card to the container
        dataLayout.addView(cardView);
    }


    private String formatDate(String date) {
        // Example date formatting logic (adjust based on your date format)
        // You can use SimpleDateFormat for more advanced formatting
        return date.replace("/", "\n");
    }
    private int getMonthNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "jan": return 1;
            case "feb": return 2;
            case "mar": return 3;
            case "apr": return 4;
            case "may": return 5;
            case "jun": return 6;
            case "jul": return 7;
            case "aug": return 8;
            case "sep": return 9;
            case "oct": return 10;
            case "nov": return 11;
            case "dec": return 12;
            default: return 1;
        }
    }



}