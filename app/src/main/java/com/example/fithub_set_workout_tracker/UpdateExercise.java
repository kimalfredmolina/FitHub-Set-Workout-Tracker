package com.example.fithub_set_workout_tracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.sets_tracker.AddExercise;
import com.example.fithub_set_workout_tracker.sets_tracker.SelectWorkout;
import com.example.fithub_set_workout_tracker.sets_tracker.Update_SelectWorkout;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class UpdateExercise extends AppCompatActivity {

    private static final String TAG = "AddExercise";

    private EditText u_program;
    private EditText u_weight;
    private EditText u_date;
    private EditText u_time;
    private EditText u_endTime;
    private EditText u_notes;
    private Button updateButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseref;
    private DatabaseReference workoutref;

    private int setCount = 1;
    private static final int MAX_SETS = 5;
    private TextView workoutName;
    private Button upExerciseButton, upSetButton;
    private LinearLayout workout_Details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_exercise);

        workoutName = findViewById(R.id.workoutname);
        upExerciseButton = findViewById(R.id.btn_update_exercise);
        workout_Details = findViewById(R.id.workout_details);
        upSetButton = findViewById(R.id.btn_add_set);


        // Get intent extras
        String date = getIntent().getStringExtra("date");
        String workoutTitle = getIntent().getStringExtra("workoutTitle");
        String workoutType = getIntent().getStringExtra("workoutType");

        if (date == null || workoutTitle == null || workoutType == null) {
            Toast.makeText(this, "Invalid data passed!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!workoutTitle.isEmpty()) {
            workoutName.setVisibility(View.VISIBLE);
            workout_Details.setVisibility(View.VISIBLE);
            runOnUiThread(() -> workoutName.setText(workoutTitle));
        }



        Log.d("UpdateExercise", "Date: " + date + ", WorkoutTitle: " + workoutTitle);




        // Validate that the data was passed correctly






        // Initialize EditTexts and Button
        u_program = findViewById(R.id.Program_update);
        u_weight = findViewById(R.id.weight_update);
        u_date = findViewById(R.id.date_picker_update);
        u_time = findViewById(R.id.time_update);
        u_endTime = findViewById(R.id.end_time_update);
        u_notes = findViewById(R.id.notes_update);
        updateButton = findViewById(R.id.update_btn);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseref = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("workout");

        // Load data from Firebase
        loadWorkoutData();

        // Set up DatePicker dialog for the date EditText
        u_date.setOnClickListener(v -> {
            Toast.makeText(this, "You can't update the date!", Toast.LENGTH_SHORT).show();
        });

        // Set up TimePicker for the start time (u_time)
        u_time.setOnClickListener(v -> showTimePicker(u_time));

        // Set up TimePicker for the end time (u_endTime)
        u_endTime.setOnClickListener(v -> showTimePicker(u_endTime));

        // Set up Update button click listener
        updateButton.setOnClickListener(v -> updateDataInFirebase());



        upExerciseButton.setOnClickListener(v -> {
            Intent selectWorkoutIntent = new Intent(UpdateExercise.this, Update_SelectWorkout.class);
            startActivityForResult(selectWorkoutIntent, 1);

        });
        upSetButton.setOnClickListener(v -> addSet());

    }

    private void addSet() {
        if (setCount > MAX_SETS) {
            upSetButton.setEnabled(false);
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        View setView = inflater.inflate(R.layout.set_layout, workout_Details, false);

        TextView setNumber = setView.findViewById(R.id.set_number);
        EditText lbField = setView.findViewById(R.id.lb_field);
        EditText repsField = setView.findViewById(R.id.reps_field);
        EditText notesField = setView.findViewById(R.id.notes_field);

        setNumber.setText(String.valueOf(setCount));
        setCount++;

        lbField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        repsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ImageButton popupMenuButton = setView.findViewById(R.id.Popup_Menu);
        popupMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(UpdateExercise.this, v);
            MenuInflater inflater1 = popupMenu.getMenuInflater();
            inflater1.inflate(R.menu.custom_menu, popupMenu.getMenu());


            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit) {
                    Toast.makeText(UpdateExercise.this, "Edit clicked for set " + setNumber.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.delete) {
                    workout_Details.removeView(setView);
                    setCount--;
                    return true;
                }
                return false;
            });
        });

        workout_Details.removeView(upSetButton);
        workout_Details.addView(setView);
        workout_Details.addView(upSetButton);
    }



    private void loadWorkoutData() {
        // ... (previous initialization code remains the same until set creation)
        String workoutTitle = getIntent().getStringExtra("workoutTitle");
        String date = getIntent().getStringExtra("date");

        if (date == null || workoutTitle == null) {
            Toast.makeText(this, "Invalid data!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        workoutref = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("workouts")
                .child(date)
                .child("1")    // Add these nodes to match your structure
                .child("2025");

        Log.d("UpdateExercise", "Database path: " + workoutref.toString());

        workoutref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("UpdateExercise", "Full snapshot: " + snapshot.getValue());
                    // ... (previous data loading code remains the same until set creation)
                    String program = snapshot.child("program").getValue(String.class);
                    String startTime = snapshot.child("startTime").getValue(String.class);
                    String endTime = snapshot.child("endTime").getValue(String.class);
                    String notes = snapshot.child("notes").getValue(String.class);


                    Log.d("UpdateExercise", "Program: " + program);
                    Log.d("UpdateExercise", "Start Time: " + startTime);

                    u_program.setText(program);
                    u_date.setText(date);
                    u_time.setText(startTime);
                    u_endTime.setText(endTime);
                    u_notes.setText(notes);

                    workoutName.setText(workoutTitle);

                    DataSnapshot exercisesSnapshot = snapshot.child("exercises").child("ex1");
                    Log.d("UpdateExercise", "Exercises exist: " + exercisesSnapshot.exists());
                    if (exercisesSnapshot.exists()) {
                        String exerciseName = exercisesSnapshot.child("name").getValue(String.class);
                        Log.d("UpdateExercise", "Exercise name: " + exerciseName);
                        if (exerciseName != null && exerciseName.equals(workoutTitle)) {
                            DataSnapshot setsSnapshot = exercisesSnapshot.child("sets");
                            for (DataSnapshot setSnapshot : setsSnapshot.getChildren()) {
                                View setView = getLayoutInflater().inflate(R.layout.set_layout, workout_Details, false);

                                ((TextView) setView.findViewById(R.id.set_number)).setText(setSnapshot.getKey());
                                ((EditText) setView.findViewById(R.id.lb_field)).setText(String.valueOf(setSnapshot.child("weight").getValue()));
                                ((EditText) setView.findViewById(R.id.reps_field)).setText(String.valueOf(setSnapshot.child("reps").getValue()));
                                ((EditText) setView.findViewById(R.id.notes_field)).setText(setSnapshot.child("notes").getValue(String.class));

                                // Add popup menu
                                ImageButton popupMenuButton = setView.findViewById(R.id.Popup_Menu);
                                popupMenuButton.setOnClickListener(v -> {
                                    PopupMenu popupMenu = new PopupMenu(UpdateExercise.this, v);
                                    MenuInflater inflater = popupMenu.getMenuInflater();
                                    inflater.inflate(R.menu.custom_menu, popupMenu.getMenu());

                                    popupMenu.setOnMenuItemClickListener(item -> {
                                        if (item.getItemId() == R.id.edit) {
                                            Toast.makeText(UpdateExercise.this,
                                                    "Edit clicked for set " + ((TextView) setView.findViewById(R.id.set_number)).getText(),
                                                    Toast.LENGTH_SHORT).show();
                                            return true;
                                        } else if (item.getItemId() == R.id.delete) {
                                            workout_Details.removeView(setView);
                                            upSetButton.setEnabled(true);
                                            return true;
                                        }
                                        return false;
                                    });

                                    popupMenu.show();
                                });

                                workout_Details.addView(setView);
                            }
                        }
                    }
                    else{
                        Log.d("UpdateExercise", "No data found at this path");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UpdateExercise", "Error loading data", error.toException());
                Toast.makeText(UpdateExercise.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDataInFirebase() {
        String date = u_date.getText().toString();
        String workoutTitle = workoutName.getText().toString();

        if (date.isEmpty() || workoutTitle.isEmpty()) {
            Toast.makeText(this, "Missing data!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("workouts")
                .child(date)
                .child("1")
                .child("2025");

        Map<String, Object> updates = new HashMap<>();
        updates.put("program", u_program.getText().toString().trim());
        updates.put("startTime", u_time.getText().toString().trim());
        updates.put("endTime", u_endTime.getText().toString().trim());
        updates.put("notes", u_notes.getText().toString().trim());

        // Create sets data
        Map<String, Object> setsData = new HashMap<>();
        for (int i = 0; i < workout_Details.getChildCount(); i++) {
            View setView = workout_Details.getChildAt(i);
            if (setView.findViewById(R.id.set_number) != null) {
                String setNumber = ((TextView)setView.findViewById(R.id.set_number)).getText().toString();
                Map<String, Object> setData = new HashMap<>();
                setData.put("weight", Double.parseDouble(((EditText)setView.findViewById(R.id.lb_field)).getText().toString()));
                setData.put("reps", Integer.parseInt(((EditText)setView.findViewById(R.id.reps_field)).getText().toString()));
                setData.put("notes", ((EditText)setView.findViewById(R.id.notes_field)).getText().toString());
                setsData.put(setNumber, setData);
            }
        }

        // Update the exercise data under ex1
        Map<String, Object> exerciseData = new HashMap<>();
        exerciseData.put("name", workoutTitle);
        exerciseData.put("sets", setsData);
        updates.put("exercises/ex1", exerciseData);

        updateRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateExercise.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateExercise", "Update failed", e);
                    Toast.makeText(UpdateExercise.this,
                            "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private String formatDateToInput(String firebaseDate) {
        SimpleDateFormat firebaseFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = firebaseFormat.parse(firebaseDate);
            return inputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return firebaseDate; // Return as-is if parsing fails
        }
    }

    private String formatDateToFirebase(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat firebaseFormat = new SimpleDateFormat("dd//MM//yy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(inputDate);
            return firebaseFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate; // Return as-is if parsing fails
        }
    }


    private void showDatePicker() {
        // Get the current date or default to today's date
        String currentDate = u_date.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();

        if (!currentDate.isEmpty()) {
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(currentDate);
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            // Format and set the selected date in EditText
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
            u_date.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker(EditText timeField) {
        // Get the current time as default
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Format the selected time
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

                    // Set the selected time in the clicked field (start or end time)
                    timeField.setText(formattedTime);
                },
                hour,
                minute,
                true // Use 24-hour format
        );

        timePickerDialog.show(); // Show the time picker
    }


}