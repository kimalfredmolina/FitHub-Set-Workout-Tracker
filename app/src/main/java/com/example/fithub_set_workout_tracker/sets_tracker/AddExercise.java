package com.example.fithub_set_workout_tracker.sets_tracker;

import android.app.AlertDialog;  //ahwh
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences; //ewfwefw
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.main_pages.SetTrackerPage;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExercise extends AppCompatActivity {

    private static final String TAG = "AddExercise";

    private TextView workoutName, date, time, endTime;
    private ImageView backButton;
    private Button addExerciseButton, addSetButton, finishButton, Btnsave;
    private LinearLayout workout_Details;
    private int setCount = 2;
    private static final int MAX_SETS = 5;
    private EditText notesfield, reps, lbs;

    private EditText program, weight, notes;
    FirebaseAuth mAuth;
    private DatabaseReference databaseref;

    private String muscleGroup = null;
    private String selectedExercise = null;

    private void saveData() {
        SharedPreferences preferences = getSharedPreferences("AddExerciseData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("selectedExercise", selectedExercise);
        editor.putString("muscleGroup", muscleGroup);
        editor.putString("notes", notes.getText().toString());
        editor.putString("weight", weight.getText().toString());
        editor.putString("date", date.getText().toString());
        editor.putString("time", time.getText().toString());
        editor.putString("endTime", endTime.getText().toString());

        editor.apply();
    }
    private void loadData() {
        SharedPreferences preferences = getSharedPreferences("AddExerciseData", MODE_PRIVATE);

        selectedExercise = preferences.getString("selectedExercise", "");
        muscleGroup = preferences.getString("muscleGroup", "");
        notes.setText(preferences.getString("notes", ""));
        weight.setText(preferences.getString("weight", ""));
        date.setText(preferences.getString("date", ""));
        time.setText(preferences.getString("time", ""));
        endTime.setText(preferences.getString("endTime", ""));

        if (!selectedExercise.isEmpty()) {
            workoutName.setVisibility(View.VISIBLE);
            workoutName.setText(selectedExercise);
            workout_Details.setVisibility(View.VISIBLE);
        } else {
            workoutName.setVisibility(View.GONE);
        }
    }

    private void clearData() {
        SharedPreferences preferences = getSharedPreferences("AddExerciseData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Reset input fields
        notes.setText("");
        weight.setText("");
        date.setText("");
        time.setText("");
        endTime.setText("");
        workoutName.setText("");
        workout_Details.removeAllViews();
        setCount = 2; // Reset set counter
        finishButton.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exercise);
        initializeViews();
        loadData();

        LayoutInflater inflaters = LayoutInflater.from(this);
        View anotherLayout = inflaters.inflate(R.layout.set_layout, null);

        mAuth = FirebaseAuth.getInstance();
        databaseref = FirebaseDatabase.getInstance().getReference("users");

        date = findViewById(R.id.date_picker_actions);
        time = findViewById(R.id.time);
        endTime = findViewById(R.id.end_time);
        workoutName = findViewById(R.id.workout_name);
        backButton = findViewById(R.id.arrow_back);
        addExerciseButton = findViewById(R.id.btn_add_exercise);
        workout_Details = findViewById(R.id.workout_details);
        addSetButton = findViewById(R.id.btn_add_set);
        finishButton = findViewById(R.id.finish_button);
        Btnsave= findViewById(R.id.save_btn);

        notesfield = anotherLayout.findViewById(R.id.notes_field);
        reps = anotherLayout.findViewById(R.id.reps_field);
        lbs = anotherLayout.findViewById(R.id.lb_field);



        program = findViewById(R.id.Program);
        weight = findViewById(R.id.weight);
        notes = findViewById(R.id.notes);

        finishButton.setVisibility(View.GONE);
        workout_Details.setVisibility(View.GONE);

        selectedExercise = getIntent().getStringExtra("selectedExercise");
        muscleGroup = getIntent().getStringExtra("selectedMuscleGroup");

        backButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Exit")
                    .setMessage("Are you sure you want to discard your workout and go back?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        clearData();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        Btnsave.setOnClickListener(v -> {
            String note = notes.getText().toString().trim();
            String weights = weight.getText().toString();
            String sets = lbs.getText().toString();
            String Reps = reps.getText().toString();
            String Date = date.getText().toString();
            String startTime = time.getText().toString();
            String Endtime = endTime.getText().toString();

            Log.d("Validation", "Sets: " + sets);
            Log.d("Validation", "Reps: " + Reps);
            Log.d("Validation", "Note: " + note);
            Log.d("Validation", "Weights: " + weights);
            Log.d("Validation", "Date: " + Date);
            Log.d("Validation", "StartTime: " + startTime);
            Log.d("Validation", "EndTime: " + Endtime);

            // Check if all fields are filled
            if (sets.isEmpty() || Reps.isEmpty() || note.isEmpty() || weights.isEmpty() || Date.isEmpty() || startTime.isEmpty() || Endtime.isEmpty()) {
                Toast.makeText(AddExercise.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                // Create the workout object
                Map<String, Object> workout = new HashMap<>();
                workout.put("exercise", selectedExercise);
                workout.put("Lbs", Integer.parseInt(sets));
                workout.put("reps", Integer.parseInt(Reps));
                workout.put("date", Date);
                workout.put("startTime", startTime);
                workout.put("endTime", Endtime);
                workout.put("SetCount", setCount - 1);

                // Generate a unique key for this workout
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseref = database.getReference();

                String key = databaseref.child(uid).child("workouts").child(muscleGroup).push().getKey();
                if (key == null) {
                    Toast.makeText(AddExercise.this, "Failed to generate key", Toast.LENGTH_SHORT).show();
                    return;
                }
                workout.put("key", key);

                // Save the workout data
                databaseref.child(uid).child("workouts").child(muscleGroup).child(key).setValue(workout)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddExercise.this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
                                clearData();
                                Intent intent = new Intent(AddExercise.this, SetTrackerPage.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(AddExercise.this, "Failed to save workout", Toast.LENGTH_SHORT).show();
                            }
                        });

                // Save weight and notes data for the user
                double userWeight = Double.parseDouble(weights);
                String userNotes = note;

                databaseref.child(uid).child("weight").setValue(userWeight);
                databaseref.child(uid).child("notes").setValue(userNotes)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddExercise.this, "Weight and notes saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddExercise.this, "Failed to save weight or notes", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        date.setOnClickListener(v -> {
            showDatePicker();
            checkIfFinishButtonShouldBeVisible();
        });

        time.setOnClickListener(v -> {
            showTimePicker(time);
            checkIfFinishButtonShouldBeVisible();
        });

        endTime.setOnClickListener(v -> {
            showTimePicker(endTime);
            checkIfFinishButtonShouldBeVisible();
        });


        Intent intent = getIntent();
        String selectedExercise = intent.getStringExtra("selectedExercise");

        if (selectedExercise != null && !selectedExercise.isEmpty()) {
            workoutName.setVisibility(View.VISIBLE);
            workoutName.setText(selectedExercise);
            workout_Details.setVisibility(View.VISIBLE);
        } else {
            workoutName.setVisibility(View.GONE);
        }

        addExerciseButton.setOnClickListener(v -> {
            Intent selectWorkoutIntent = new Intent(AddExercise.this, SelectWorkout.class);
            startActivityForResult(selectWorkoutIntent, 1);

        });

        addSetButton.setOnClickListener(v -> addSet());



        ImageButton popupMenuButton = findViewById(R.id.Popup_menu);
        popupMenuButton.setOnClickListener(v -> {
        PopupMenu popupMenu = new PopupMenu(AddExercise.this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.custom_menu, popupMenu.getMenu());

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.edit) {
                Toast.makeText(AddExercise.this, "Edit clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.delete) {
                Toast.makeText(AddExercise.this, "Delete clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;

        });
        });
    }

    private void initializeViews() {
        date = findViewById(R.id.date_picker_actions);
        time = findViewById(R.id.time);
        endTime = findViewById(R.id.end_time);
        workoutName = findViewById(R.id.workout_name);

        backButton = findViewById(R.id.arrow_back);
        addExerciseButton = findViewById(R.id.btn_add_exercise);
        addSetButton = findViewById(R.id.btn_add_set);
        finishButton = findViewById(R.id.finish_button);
        Btnsave = findViewById(R.id.save_btn);

        workout_Details = findViewById(R.id.workout_details);

        notes = findViewById(R.id.notes);
        weight = findViewById(R.id.weight);
        program = findViewById(R.id.Program);
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Are you sure you want to discard your workout and go back?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearData();
                    super.onBackPressed();
                    Intent intent = new Intent(AddExercise.this, SetTrackerPage.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddExercise.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    date.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    checkIfFinishButtonShouldBeVisible();
                },
                year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void showTimePicker(final TextView timeField) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddExercise.this,
                (view, selectedHour, selectedMinute) -> {
                    timeField.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    checkIfFinishButtonShouldBeVisible();
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    private void addSet() {
        if (setCount > MAX_SETS) {
            addSetButton.setEnabled(false);
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

        // Attach TextWatchers to dynamically added fields
        lbField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfFinishButtonShouldBeVisible();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        repsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfFinishButtonShouldBeVisible();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add popup menu for this specific set
        ImageButton popupMenuButton = setView.findViewById(R.id.Popup_Menu);
        popupMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(AddExercise.this, v);
            MenuInflater inflater1 = popupMenu.getMenuInflater();
            inflater1.inflate(R.menu.custom_menu, popupMenu.getMenu());

            // Show the PopupMenu
            popupMenu.show();

            // Handle item clicks using if-else
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit) {
                    // Handle edit action
                    Toast.makeText(AddExercise.this, "Edit clicked for set " + setNumber.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.delete) {
                    workout_Details.removeView(setView);  // Remove the set view
                    setCount--;
                    checkIfFinishButtonShouldBeVisible();  // Revalidate finish button visibility
                    return true;
                }
                return false;
            });
        });

        workout_Details.removeView(addSetButton);
        workout_Details.addView(setView);
        workout_Details.addView(addSetButton);
        checkIfFinishButtonShouldBeVisible();
    }

    private void checkIfFinishButtonShouldBeVisible() {
        // Retrieve the values of all relevant fields and check if they are empty
        for (int i = 0; i < workout_Details.getChildCount(); i++) {
            View child = workout_Details.getChildAt(i);
            EditText lbField = child.findViewById(R.id.lb_field);
            EditText repsField = child.findViewById(R.id.reps_field);

            if (lbField != null && repsField != null) {
                String lbs = lbField.getText().toString().trim();
                String reps = repsField.getText().toString().trim();

                if (lbs.isEmpty() || reps.isEmpty()) {
                    finishButton.setVisibility(View.GONE);
                    return;
                }
            }
        }


        boolean isAnySetFieldEmpty = false;
        for (int i = 0; i < workout_Details.getChildCount(); i++) {
            View setView = workout_Details.getChildAt(i);
            if (setView instanceof LinearLayout) {
                EditText setRepsField = setView.findViewById(R.id.reps_field);
                EditText setLbsField = setView.findViewById(R.id.lb_field);

                if (setRepsField != null && setLbsField != null) {
                    String repsText = setRepsField.getText().toString().trim();
                    String lbsText = setLbsField.getText().toString().trim();

                    if (repsText.isEmpty() || lbsText.isEmpty()) {
                        isAnySetFieldEmpty = true;
                        break;  // If any field in a set is empty, don't show the finish button
                    }
                }
            }
        }

        // Check if all required fields have data entered
        if (!program.getText().toString().isEmpty() &&
                !weight.getText().toString().isEmpty() &&
                !notes.getText().toString().isEmpty() &&
                !date.getText().toString().isEmpty() &&
                !time.getText().toString().isEmpty() &&
                !endTime.getText().toString().isEmpty()) {
            finishButton.setVisibility(View.VISIBLE); // Show the finish button
        } else {
            finishButton.setVisibility(View.GONE); // Hide the finish button
        }
    }

}