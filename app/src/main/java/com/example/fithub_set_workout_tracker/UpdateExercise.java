package com.example.fithub_set_workout_tracker;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.main_pages.SetTrackerPage;
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
    private String originalExerciseName;
    private String originalMuscleGroup;
    private View arrow_back_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_exercise);

        workoutName = findViewById(R.id.workoutname);
        upExerciseButton = findViewById(R.id.btn_update_exercise);
        workout_Details = findViewById(R.id.workout_details);
        upSetButton = findViewById(R.id.btn_add_set);

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

        originalExerciseName = getIntent().getStringExtra("workoutTitle");
        originalMuscleGroup = getIntent().getStringExtra("workoutType");

        // initialize EditTexts and Button
        u_program = findViewById(R.id.Program_update);
        u_weight = findViewById(R.id.weight_update);
        u_date = findViewById(R.id.date_picker_update);
        u_time = findViewById(R.id.time_update);
        u_endTime = findViewById(R.id.end_time_update);
        u_notes = findViewById(R.id.notes_update);
        updateButton = findViewById(R.id.update_btn);

        // initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseref = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("workout");

        loadWorkoutData();

        u_date.setOnClickListener(v -> {
            Toast.makeText(this, "You can't update the date!", Toast.LENGTH_SHORT).show();
        });
        u_time.setOnClickListener(v -> showTimePicker(u_time));
        u_endTime.setOnClickListener(v -> showTimePicker(u_endTime));
        updateButton.setOnClickListener(v -> updateDataInFirebase());


        // pass the original exercise details to potentially use in comparison
        upExerciseButton.setOnClickListener(v -> {
            Intent selectWorkoutIntent = new Intent(UpdateExercise.this, Update_SelectWorkout.class);
            selectWorkoutIntent.putExtra("originalExercise", originalExerciseName);
            selectWorkoutIntent.putExtra("originalMuscleGroup", originalMuscleGroup);
            startActivityForResult(selectWorkoutIntent, 1);
        });

        arrow_back_update = findViewById(R.id.arrow_back_update);
        arrow_back_update.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateExercise.this, MainPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        upSetButton.setOnClickListener(v -> addSet());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String newExercise = data.getStringExtra("selectedExercise");
            String newMuscleGroup = data.getStringExtra("selectedMuscleGroup");

            // Update the workout name display
            workoutName.setText(newExercise);

            // Store these new values to use when updating
            originalExerciseName = newExercise;
            originalMuscleGroup = newMuscleGroup;
        }
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
                if (item.getItemId() == R.id.delete) {
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


    //for fetching data from set tracker to update form
    private void loadWorkoutData() {
        String workoutTitle = getIntent().getStringExtra("workoutTitle");
        String date = getIntent().getStringExtra("date");

        if (date == null || workoutTitle == null) {
            Toast.makeText(this, "Invalid data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // parse the date string to get year, month, day
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date parsedDate = inputFormat.parse(date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);

            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
            String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

            String userId = mAuth.getCurrentUser().getUid();
            workoutref = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("workouts")
                    .child(year)
                    .child(month)
                    .child(day);

            Log.d("UpdateExercise", "Database path: " + workoutref.toString());

            workoutref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.d("UpdateExercise", "Full snapshot: " + snapshot.getValue());

                        String program = snapshot.child("program").getValue(String.class);
                        String startTime = snapshot.child("startTime").getValue(String.class);
                        String endTime = snapshot.child("endTime").getValue(String.class);
                        String notes = snapshot.child("notes").getValue(String.class);

                        Log.d("UpdateExercise", "Program: " + program);
                        Log.d("UpdateExercise", "Start Time: " + startTime);

                        if (program != null) u_program.setText(program);
                        u_date.setText(date);
                        if (startTime != null) u_time.setText(startTime);
                        if (endTime != null) u_endTime.setText(endTime);
                        if (notes != null) u_notes.setText(notes);

                        workoutName.setText(workoutTitle);

                        // look through all exercises to find the matching one
                        DataSnapshot exercisesSnapshot = snapshot.child("exercises");
                        for (DataSnapshot exerciseSnapshot : exercisesSnapshot.getChildren()) {
                            String exerciseName = exerciseSnapshot.child("name").getValue(String.class);
                            Log.d("UpdateExercise", "Checking exercise: " + exerciseName);

                            if (exerciseName != null && exerciseName.equals(workoutTitle)) {
                                // if found the matching exercise
                                DataSnapshot setsSnapshot = exerciseSnapshot.child("sets");
                                for (DataSnapshot setSnapshot : setsSnapshot.getChildren()) {
                                    View setView = getLayoutInflater().inflate(R.layout.set_layout, workout_Details, false);

                                    ((TextView) setView.findViewById(R.id.set_number)).setText(setSnapshot.getKey());

                                    Object weightValue = setSnapshot.child("weight").getValue();
                                    if (weightValue != null) {
                                        ((EditText) setView.findViewById(R.id.lb_field))
                                                .setText(String.valueOf(weightValue));
                                    }

                                    Object repsValue = setSnapshot.child("reps").getValue();
                                    if (repsValue != null) {
                                        ((EditText) setView.findViewById(R.id.reps_field))
                                                .setText(String.valueOf(repsValue));
                                    }

                                    String setNotes = setSnapshot.child("notes").getValue(String.class);
                                    if (setNotes != null) {
                                        ((EditText) setView.findViewById(R.id.notes_field))
                                                .setText(setNotes);
                                    }

                                    // add the popup menu
                                    setupPopupMenu(setView);

                                    workout_Details.addView(setView);
                                    setCount++; // increment set count to keep track of total sets
                                }
                                break;
                            }
                        }
                    } else {
                        Log.d("UpdateExercise", "No data found at this path");
                        Toast.makeText(UpdateExercise.this, "No workout data found for this date", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UpdateExercise", "Error loading data", error.toException());
                    Toast.makeText(UpdateExercise.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (ParseException e) {
            Log.e("UpdateExercise", "Error parsing date", e);
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    //setup popup menu
    private void setupPopupMenu(View setView) {
        ImageButton popupMenuButton = setView.findViewById(R.id.Popup_Menu);
        popupMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(UpdateExercise.this, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.custom_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.delete) {
                    workout_Details.removeView(setView);
                    setCount--;
                    upSetButton.setEnabled(true);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    //update function for firebase
    private void updateDataInFirebase() {
        String date = u_date.getText().toString();
        String workoutTitle = workoutName.getText().toString();

        if (date.isEmpty() || workoutTitle.isEmpty()) {
            Toast.makeText(this, "Missing data!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date parsedDate = inputFormat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);

            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
            String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

            String userId = mAuth.getCurrentUser().getUid();
            workoutref = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("workouts")
                    .child(year)
                    .child(month)
                    .child(day);

            // First, get the current data
            workoutref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Map<String, Object> workoutData = (Map<String, Object>) snapshot.getValue();
                        Map<String, Object> exercises = (Map<String, Object>) workoutData.get("exercises");

                        // Find the exercise to update
                        String exerciseKeyToUpdate = null;
                        for (Map.Entry<String, Object> entry : exercises.entrySet()) {
                            Map<String, Object> exercise = (Map<String, Object>) entry.getValue();
                            if (originalExerciseName.equals(exercise.get("name"))) {
                                exerciseKeyToUpdate = entry.getKey();
                                break;
                            }
                        }

                        if (exerciseKeyToUpdate != null) {
                            // create updated workout/exercise data
                            Map<String, Object> updatedExercise = new HashMap<>();
                            updatedExercise.put("name", workoutTitle);
                            updatedExercise.put("muscleGroup", originalMuscleGroup);

                            // collect sets data
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
                            updatedExercise.put("sets", setsData);

                            // update the specific exercise
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("exercises/" + exerciseKeyToUpdate, updatedExercise);
                            updates.put("program", u_program.getText().toString().trim());
                            updates.put("startTime", u_time.getText().toString().trim());
                            updates.put("endTime", u_endTime.getText().toString().trim());
                            updates.put("notes", u_notes.getText().toString().trim());

                            workoutref.updateChildren(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(UpdateExercise.this, "Exercise updated successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("UpdateExercise", "Update failed", e);
                                        Toast.makeText(UpdateExercise.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    });
                        }
                    }
                }
            });
        } catch (ParseException e) {
            Log.e("UpdateExercise", "Error parsing date", e);
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePicker(EditText timeField) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    timeField.setText(formattedTime);
                },
                hour,
                minute,
                true // Using 24-hour format
        );

        timePickerDialog.show();
    }


}