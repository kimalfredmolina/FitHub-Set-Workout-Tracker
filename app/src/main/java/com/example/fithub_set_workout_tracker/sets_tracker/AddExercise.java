package com.example.fithub_set_workout_tracker.sets_tracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.fithub_set_workout_tracker.MainPage;
import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.main_pages.SetTrackerPage;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AddExercise extends AppCompatActivity {

    private static final String TAG = "AddExercise";

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private TextView workoutName, date, time, endTime;
    private ImageView backButton;
    private Button addExerciseButton, addSetButton, finishButton, Btnsave;
    private LinearLayout workout_Details;
    private int setCount = 1;
    private static final int MAX_SETS = 5;
    private EditText notesfield, reps, lbs;

    private EditText program, weight, notes;
    FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private DatabaseReference workoutref;

    private String muscleGroup = null;
    private String selectedExercise = null;

    private List<View> inflatedViews = new ArrayList<>();




    private boolean isWorkoutSaved = false;

    private void updateButtonVisibility() {
        if (isWorkoutSaved) {
            finishButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
        } else {
            finishButton.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
        }
    }

    //for save intent when navigating throughout another activity
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

        notes.setText("");
        weight.setText("");
        date.setText("");
        time.setText("");
        endTime.setText("");
        workoutName.setText("");
        workout_Details.removeAllViews();
        setCount = 2;
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

        updateButtonVisibility();

        mAuth = FirebaseAuth.getInstance();


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

        finishButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SetTrackerPage setTrackerPage = new SetTrackerPage();
            transaction.replace(R.id.fragment_container, setTrackerPage);
            transaction.commit();
        });

        backButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Exit")
                    .setMessage("Are you sure you want to discard your workout and go back?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        clearData();
                        Intent intent = new Intent(this, MainPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        Btnsave.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();

            // get all input values
            String programText = program.getText().toString().trim();
            String bodyWeight = weight.getText().toString().trim();
            String startTime = time.getText().toString().trim();
            String endTimeValue = endTime.getText().toString().trim();
            String note = notes.getText().toString().trim();
            String dateValue = date.getText().toString().trim();

            // input validation
            if (programText.isEmpty() || bodyWeight.isEmpty() || startTime.isEmpty() ||
                    endTimeValue.isEmpty() || note.isEmpty() || dateValue.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inflatedViews.isEmpty()) {
                Toast.makeText(this, "Please add at least one exercise set.", Toast.LENGTH_SHORT).show();
                return;
            }

            // user authentication check
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = user.getUid();

            // it will get the muscle group and exercise
            String selectedMuscleGroup = getIntent().getStringExtra("selectedMuscleGroup");
            String selectedExercise = getIntent().getStringExtra("selectedExercise");

            if (selectedMuscleGroup == null || selectedExercise == null) {
                Toast.makeText(this, "Muscle group or exercise not selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse date for new structure
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date parsedDate = inputFormat.parse(dateValue);

                // Format date components
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.US);
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.US);
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.US);

                String year = yearFormat.format(parsedDate);
                String month = monthFormat.format(parsedDate);
                String day = dayFormat.format(parsedDate);

                // update reference path
                userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(uid);

                // save body weight to profile with formatted date
                userRef.child("profile")
                        .child("bodyWeight")
                        .child(dateValue)
                        .setValue(Double.parseDouble(bodyWeight));

                // reference to the specific date's workout
                DatabaseReference workoutRef = userRef.child("workouts")
                        .child(year)
                        .child(month)
                        .child(day);

                // first, check if a workout exists for this date
                workoutRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        String workoutId;
                        Map<String, Object> workoutData = new HashMap<>();

                        if (snapshot.exists()) {
                            // workout exists for this date - get existing data
                            workoutData = (Map<String, Object>) snapshot.getValue();
                            workoutId = (String) workoutData.get("workoutId");

                            // get existing exercises or create new map
                            Map<String, Object> existingExercises = (Map<String, Object>) workoutData.get("exercises");
                            if (existingExercises == null) {
                                existingExercises = new HashMap<>();
                            }

                            // generate new exercise key (ex1, ex2, etc.)
                            String newExKey = "ex" + (existingExercises.size() + 1);

                            // prepare new exercise sets
                            Map<String, Object> sets = new HashMap<>();
                            for (int i = 0; i < inflatedViews.size(); i++) {
                                View inflatedView = inflatedViews.get(i);
                                EditText lbField = inflatedView.findViewById(R.id.lb_field);
                                EditText repsField = inflatedView.findViewById(R.id.reps_field);
                                EditText exerciseNoteField = inflatedView.findViewById(R.id.notes_field);

                                String lbs = lbField.getText().toString().trim();
                                String reps = repsField.getText().toString().trim();
                                String exerciseNote = exerciseNoteField.getText().toString().trim();

                                if (lbs.isEmpty() || reps.isEmpty()) {
                                    Toast.makeText(AddExercise.this, "Please fill out all fields in each set.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Map<String, Object> setData = new HashMap<>();
                                setData.put("weight", Double.parseDouble(lbs));
                                setData.put("reps", Integer.parseInt(reps));
                                setData.put("notes", exerciseNote);
                                sets.put(String.valueOf(i + 1), setData);
                            }

                            // create new exercise object
                            Map<String, Object> newExercise = new HashMap<>();
                            newExercise.put("name", selectedExercise);
                            newExercise.put("muscleGroup", selectedMuscleGroup);
                            newExercise.put("sets", sets);

                            // add new exercise to existing exercises
                            existingExercises.put(newExKey, newExercise);
                            workoutData.put("exercises", existingExercises);

                        } else {
                            // if no workout exists for this date - create new workout
                            workoutId = workoutRef.push().getKey();

                            // prepare exercise sets
                            Map<String, Object> sets = new HashMap<>();
                            for (int i = 0; i < inflatedViews.size(); i++) {
                                View inflatedView = inflatedViews.get(i);
                                EditText lbField = inflatedView.findViewById(R.id.lb_field);
                                EditText repsField = inflatedView.findViewById(R.id.reps_field);
                                EditText exerciseNoteField = inflatedView.findViewById(R.id.notes_field);

                                String lbs = lbField.getText().toString().trim();
                                String reps = repsField.getText().toString().trim();
                                String exerciseNote = exerciseNoteField.getText().toString().trim();

                                if (lbs.isEmpty() || reps.isEmpty()) {
                                    Toast.makeText(AddExercise.this, "Please fill out all fields in each set.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Map<String, Object> setData = new HashMap<>();
                                setData.put("weight", Double.parseDouble(lbs));
                                setData.put("reps", Integer.parseInt(reps));
                                setData.put("notes", exerciseNote);
                                sets.put(String.valueOf(i + 1), setData);
                            }

                            // create first exercise object
                            Map<String, Object> exercise = new HashMap<>();
                            exercise.put("name", selectedExercise);
                            exercise.put("muscleGroup", selectedMuscleGroup);
                            exercise.put("sets", sets);

                            // create exercises map
                            Map<String, Object> exercises = new HashMap<>();
                            exercises.put("ex1", exercise);

                            // create complete workout data
                            workoutData.put("workoutId", workoutId);
                            workoutData.put("program", programText);
                            workoutData.put("startTime", startTime);
                            workoutData.put("endTime", endTimeValue);
                            workoutData.put("notes", note);
                            workoutData.put("exercises", exercises);
                            workoutData.put("date", dateValue);
                        }

                        // Save workout data
                        workoutRef.setValue(workoutData)
                                .addOnCompleteListener(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        isWorkoutSaved = true;
                                        updateButtonVisibility();
                                        Toast.makeText(AddExercise.this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AddExercise.this, MainPage.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e("AddExercise", "Failed to save workout", saveTask.getException());
                                        Toast.makeText(AddExercise.this, "Failed to save workout", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e("AddExercise", "Failed to check existing workout", task.getException());
                        Toast.makeText(AddExercise.this, "Failed to check existing workout", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (ParseException e) {
                Log.e("AddExercise", "Error parsing date", e);
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        });

        date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the date as YYYY-MM-DD
                        String selectedDate = String.format(Locale.US, "%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        date.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        time.setOnClickListener(v -> {
            showTimePicker(time);
            checkIfFinishButtonShouldBeVisible();
        });

        endTime.setOnClickListener(v -> {
            showTimePicker(endTime);
            checkIfFinishButtonShouldBeVisible();
        });

        //for selecting workouts and exercise
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

    //Dont need for now
    /*@Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Are you sure you want to discard your workout and go back?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearData();
                    super.onBackPressed();
                    Intent intent = new Intent(AddExercise.this, MainPage.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }*/

    /*private void showDatePicker() {
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
    }*/

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

    //it will add dynamically and it will add to the firebase the inflated data's
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

        // Add dynamically
        inflatedViews.add(setView);

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

        ImageButton popupMenuButton = setView.findViewById(R.id.Popup_Menu);
        popupMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(AddExercise.this, v);
            MenuInflater inflater1 = popupMenu.getMenuInflater();
            inflater1.inflate(R.menu.custom_menu, popupMenu.getMenu());


            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.delete) {
                    workout_Details.removeView(setView);
                    inflatedViews.remove(setView);
                    setCount--;
                    checkIfFinishButtonShouldBeVisible();
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
        updateButtonVisibility();
    }

}