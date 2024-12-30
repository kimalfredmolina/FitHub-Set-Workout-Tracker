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

        // Get intent extras
        String date = getIntent().getStringExtra("date");
        String workoutTitle = getIntent().getStringExtra("workoutTitle");
        String workoutType = getIntent().getStringExtra("workoutType");

        if (date == null || workoutTitle == null || workoutType == null) {
            Toast.makeText(this, "Invalid data passed!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("UpdateExercise", "Date: " + date + ", WorkoutTitle: " + workoutTitle);


        // Validate that the data was passed correctly

        workoutName = findViewById(R.id.workout_name);
        upExerciseButton = findViewById(R.id.btn_update_exercise);
        workout_Details = findViewById(R.id.workout_details);
        upSetButton = findViewById(R.id.btn_add_set);



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

        Intent intent = getIntent();
        String selectedExercise = intent.getStringExtra("selectedExercise");

        if (selectedExercise != null && !selectedExercise.isEmpty()) {
            workoutName.setVisibility(View.VISIBLE);
            workoutName.setText(selectedExercise);
            workout_Details.setVisibility(View.VISIBLE);
        } else {
            workoutName.setVisibility(View.GONE);
        }

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

    private void loadWorkoutData() {
        String date = getIntent().getStringExtra("date");
        String workoutTitle = getIntent().getStringExtra("workoutTitle");
        String workoutType = getIntent().getStringExtra("workoutType");

        Log.d("UpdateExercise", "Loading data for - " +
                "date: " + date +
                ", workoutTitle: " + workoutTitle +
                ", workoutType: " + workoutType);

        if (date == null || workoutTitle == null || workoutType == null) {
            Log.e("UpdateExercise", "Invalid data for loading");
            Toast.makeText(this, "Invalid data!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] dateParts = date.split("/");
        if (dateParts.length != 3) {
            Log.e("UpdateExercise", "Invalid date format: " + date);
            Toast.makeText(this, "Invalid date format!", Toast.LENGTH_SHORT).show();
            return;
        }

        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];

        Log.d("UpdateExercise", "Parsed date - day: " + day + ", month: " + month + ", year: " + year);

        String userId = mAuth.getCurrentUser().getUid();
        workoutref = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .child("workout")
                .child(day)
                .child(month)
                .child(year)
                .child(workoutType)
                .child(workoutTitle);

        Log.d("UpdateExercise", "Database path: " + workoutref.toString());

        workoutref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("UpdateExercise", "Data found: " + snapshot.getValue());

                    // Get values from snapshot
                    String program = snapshot.child("program").getValue(String.class);
                    String bodyWeight = snapshot.child("bodyWeight").getValue(String.class);
                    String startTime = snapshot.child("startTime").getValue(String.class);
                    String endTimeValue = snapshot.child("endTime").getValue(String.class);
                    String note = snapshot.child("note").getValue(String.class);

                    // Set values to EditText fields
                    runOnUiThread(() -> {
                        u_program.setText(program);
                        u_weight.setText(bodyWeight);
                        u_date.setText(date);
                        u_time.setText(startTime);
                        u_endTime.setText(endTimeValue);
                        u_notes.setText(note);
                    });
                } else {
                    Log.e("UpdateExercise", "No data found at path: " + workoutref.toString());
                    Toast.makeText(UpdateExercise.this, "No data found for this workout.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UpdateExercise", "Database error: " + error.getMessage());
                Toast.makeText(UpdateExercise.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void updateDataInFirebase() {
        // Get the workout type and title from Intent extras
        String workoutType = getIntent().getStringExtra("workoutTitle");  // Changed to match save code
        String workoutTitle = getIntent().getStringExtra("workoutType");    // Changed to match save code

        // Validate workout type and title
        if (workoutType == null || workoutTitle == null) {
            Toast.makeText(this, "Missing workout information!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve updated values from EditTexts
        String program = u_program.getText().toString().trim();
        String bodyWeight = u_weight.getText().toString().trim();
        String dateValue = u_date.getText().toString().trim();
        String startTime = u_time.getText().toString().trim();
        String endTime = u_endTime.getText().toString().trim();
        String note = u_notes.getText().toString().trim();

        // Validate the data
        if (program.isEmpty() || bodyWeight.isEmpty() || dateValue.isEmpty() ||
                startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Use the same reference structure as your save code
        workoutref = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .child("workout")
                .child(dateValue)           // Use full date as one child
                .child(workoutType)         // selectedMuscleGroup
                .child(workoutTitle);       // selectedExercise

        // Create a map for the updates
        Map<String, Object> updates = new HashMap<>();
        updates.put("program", program);
        updates.put("bodyWeight", bodyWeight);
        updates.put("startTime", startTime);
        updates.put("endTime", endTime);
        updates.put("note", note);

        // If you have sets data to update, include it here
        // updates.put("sets", setsData);  // Uncomment and modify if updating sets

        // Update all values at once
        workoutref.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateExercise.this,
                            "Workout updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Optional: close the activity after successful update
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdateExercise.this,
                            "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

}