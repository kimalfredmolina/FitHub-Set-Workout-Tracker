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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SetTrackerPage extends Fragment {

    private LinearLayout dataLayout;
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
        dataLayout = view.findViewById(R.id.exercise_container);

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
        databaseReference.child("users")
                .child(userId)
                .child("workouts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Iterate through years, months, and days
                            for (DataSnapshot yearSnapshot : snapshot.getChildren()) {
                                for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                                        // construct the full date
                                        String year = yearSnapshot.getKey();
                                        String month = monthSnapshot.getKey();
                                        String day = daySnapshot.getKey();
                                        String fullDate = String.format("%s-%s-%s", year, month, day);

                                        // get workout data
                                        String program = daySnapshot.child("program").getValue(String.class);

                                        StringBuilder details = new StringBuilder();
                                        DataSnapshot exercisesSnapshot = daySnapshot.child("exercises");

                                        // count total exercises and sets
                                        int totalExercises = 0;
                                        int totalSets = 0;

                                        // it will iterate through all exercises (ex1, ex2, etc.)
                                        for (DataSnapshot exerciseSnapshot : exercisesSnapshot.getChildren()) {
                                            totalExercises++;
                                            String exerciseName = exerciseSnapshot.child("name").getValue(String.class);
                                            DataSnapshot setsSnapshot = exerciseSnapshot.child("sets");
                                            int setCount = (int) setsSnapshot.getChildrenCount();
                                            totalSets += setCount;

                                            if (exerciseName != null) {
                                                if (details.length() > 0) {
                                                    details.append("\n");
                                                }
                                                details.append(exerciseName)
                                                        .append(" x")
                                                        .append(setCount)
                                                        .append(" sets");
                                            }
                                        }

                                        // add time information
                                        String startTime = daySnapshot.child("startTime").getValue(String.class);
                                        String endTime = daySnapshot.child("endTime").getValue(String.class);
                                        if (startTime != null && endTime != null) {
                                            if (details.length() > 0) {
                                                details.append("\n");
                                            }
                                            details.append(startTime)
                                                    .append(" - ")
                                                    .append(endTime);
                                        }


                                        // format the date for display
                                        String formattedDate = formatDate(fullDate);

                                        // add summary of total exercises and sets
                                        String summaryTitle = String.format("%s (%d exercises)",
                                                program != null ? program : "Workout",
                                                totalExercises,
                                                totalSets);

                                        addWorkoutCard(formattedDate,
                                                summaryTitle,
                                                details.toString().trim());
                                    }
                                }
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
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d", Locale.US);
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("SetTrackerPage", "Error formatting date: " + dateStr, e);
            return dateStr;
        }
    }



    private void addWorkoutCard(String date, String workoutTitle, String workoutDetails) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.workout_item, dataLayout, false);

        TextView dateText = cardView.findViewById(R.id.date_text); // Set date
        if (dateText != null) {
            dateText.setText(date);
        }

        TextView workoutTitleText = cardView.findViewById(R.id.workout_title); // Set workout title
        if (workoutTitleText != null) {
            workoutTitleText.setText(workoutTitle);
        }

        TextView workoutDetailsText = cardView.findViewById(R.id.workout_details); // Set workout details
        if (workoutDetailsText != null) {
            workoutDetailsText.setText(workoutDetails);
        }

        // it will make the card clickable and open the workout details
        cardView.setOnClickListener(v -> {
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM d", Locale.US);
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                // parse the display date and convert to ISO format
                Date parsedDate = displayFormat.parse(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(parsedDate);
                cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                String isoDate = isoFormat.format(cal.getTime());

                // it will split the workout details into an array of exercises
                String[] exercises = workoutDetails.split("\n");
                String firstExercise = exercises[0].split(" x")[0];

                Intent intent = new Intent(getContext(), UpdateExercise.class);
                intent.putExtra("date", isoDate);
                intent.putExtra("workoutTitle", firstExercise);
                intent.putExtra("workoutType", workoutTitle);
                startActivity(intent);

            } catch (ParseException e) {
                Log.e("SetTrackerPage", "Error parsing date", e);
                Toast.makeText(getContext(), "Error opening workout details", Toast.LENGTH_SHORT).show();
            }
        });

        //for deleting the workout
        cardView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete Workout Data Card")
                    .setMessage("Are you sure you want to delete this workout?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        String userId = mAuth.getCurrentUser().getUid();

                        try {
                            // Convert display date (MMM d) to ISO format (yyyy-MM-dd)
                            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM d", Locale.US);
                            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                            Date parsedDate = displayFormat.parse(date);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(parsedDate);
                            cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

                            // Format the date components
                            String year = String.valueOf(cal.get(Calendar.YEAR));
                            String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
                            String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

                            // Create database reference with the correct path
                            databseref = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(userId)
                                    .child("workouts")
                                    .child(year)
                                    .child(month)
                                    .child(day);

                            databseref.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        dataLayout.removeView(cardView);
                                        Toast.makeText(getContext(), "Workout deleted successfully", Toast.LENGTH_SHORT).show();
                                        Log.d("SetTrackerPage", "Successfully deleted workout");
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to delete workout: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        Log.e("SetTrackerPage", "Failed to delete workout", e);
                                    });

                        } catch (ParseException e) {
                            Log.e("SetTrackerPage", "Error parsing date", e);
                            Toast.makeText(getContext(), "Error deleting workout: Invalid date format", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();

            return true;
        });
        dataLayout.addView(cardView);
    }
}