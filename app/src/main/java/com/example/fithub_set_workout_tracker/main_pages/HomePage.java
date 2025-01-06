package com.example.fithub_set_workout_tracker.main_pages;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.fithub_set_workout_tracker.LoginForm;
import com.example.fithub_set_workout_tracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class HomePage extends Fragment {
    private View rootView;
    private TextView totalWorkoutsText, streakText, programText;
    private CalendarView calendarView;
    private DatabaseReference userRef;
    private String userId;
    private SimpleDateFormat dateFormat;
    private Button startWorkoutBtn, logWorkoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(requireActivity(), LoginForm.class));
            requireActivity().finish();
            return rootView;
        }

        userId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        initializeViews();
        setupCalendar();

        loadMonthlyStats();

        return rootView;
    }

    private void initializeViews() {
        totalWorkoutsText = rootView.findViewById(R.id.total_workouts_text);
        streakText = rootView.findViewById(R.id.streak_text);
        programText = rootView.findViewById(R.id.program_text);
        calendarView = rootView.findViewById(R.id.calendar_view);
        startWorkoutBtn = rootView.findViewById(R.id.start_workout_btn);
        logWorkoutBtn = rootView.findViewById(R.id.log_workout_btn);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    private void setupCalendar() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            String dateStr = dateFormat.format(selectedDate.getTime());
            loadWorkoutForDate(dateStr);
        });
    }

    private void loadMonthlyStats() {
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1);

        userRef.child("workouts").child(year).child(month)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalWorkouts = 0;
                        Map<String, Integer> programCounts = new HashMap<>();

                        for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                            totalWorkouts++;
                            String program = daySnapshot.child("program").getValue(String.class);
                            if (program != null) {
                                programCounts.put(program, programCounts.getOrDefault(program, 0) + 1);
                            }
                        }

                        totalWorkoutsText.setText(String.valueOf(totalWorkouts));
                        updateStreak(snapshot);
                        updateMostFrequentProgram(programCounts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "Failed to load stats", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStreak(DataSnapshot monthSnapshot) {
        Calendar cal = Calendar.getInstance();
        int streak = 0;
        boolean broken = false;

        for (int i = 0; i < 30; i++) {
            String dateStr = String.format(Locale.US, "%02d", cal.get(Calendar.DATE));

            if (monthSnapshot.hasChild(dateStr)) {
                if (!broken) streak++;
            } else {
                broken = true;
            }

            cal.add(Calendar.DATE, -1);
        }

        streakText.setText(String.format(Locale.US, "%d days", streak));
    }

    private void updateMostFrequentProgram(Map<String, Integer> programCounts) {
        if (programCounts.isEmpty()) {
            programText.setText("No programs yet");
            return;
        }

        String mostFrequent = Collections.max(programCounts.entrySet(),
                Map.Entry.comparingByValue()).getKey();
        programText.setText(mostFrequent);
    }



    private void loadWorkoutForDate(String date) {
        String[] dateParts = date.split("-");
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];

        userRef.child("workouts").child(year).child(month).child(day)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String program = snapshot.child("program").getValue(String.class);
                            String startTime = snapshot.child("startTime").getValue(String.class);
                            showWorkoutDetails(program, startTime, snapshot);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "Failed to load workout", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showWorkoutDetails(String program, String startTime, DataSnapshot workout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Workout Details");

        StringBuilder details = new StringBuilder();
        details.append("Program: ").append(program).append("\n");
        details.append("Start Time: ").append(startTime).append("\n\n");

        DataSnapshot exercisesSnapshot = workout.child("exercises");
        for (DataSnapshot exerciseSnapshot : exercisesSnapshot.getChildren()) {
            String name = exerciseSnapshot.child("name").getValue(String.class);
            details.append(name).append(":\n");

            DataSnapshot setsSnapshot = exerciseSnapshot.child("sets");
            for (DataSnapshot setSnapshot : setsSnapshot.getChildren()) {
                double weight = setSnapshot.child("weight").getValue(Double.class);
                int reps = setSnapshot.child("reps").getValue(Integer.class);
                details.append("- ").append(weight).append("lbs x ").append(reps).append(" reps\n");
            }
            details.append("\n");
        }

        builder.setMessage(details.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMonthlyStats();
    }
}