package com.example.fithub_set_workout_tracker.sets_tracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;
import com.example.fithub_set_workout_tracker.SetTrackerPage;

import java.util.Calendar;

public class AddExercise extends AppCompatActivity {

    private static final String TAG = "AddExercise";

    private TextView workoutName, date, time, endTime;
    private ImageView backButton;
    private Button addExerciseButton, addSetButton, finishButton;
    private LinearLayout workout_Details;
    private int setCount = 2;
    private static final int MAX_SETS = 5;

    private EditText program, weight, notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exercise);

        date = findViewById(R.id.date_picker_actions);
        time = findViewById(R.id.time);
        endTime = findViewById(R.id.end_time);
        workoutName = findViewById(R.id.workout_name);
        backButton = findViewById(R.id.arrow_back);
        addExerciseButton = findViewById(R.id.btn_add_exercise);
        workout_Details = findViewById(R.id.workout_details);
        addSetButton = findViewById(R.id.btn_add_set);
        finishButton = findViewById(R.id.finish_button);

        program = findViewById(R.id.Program);
        weight = findViewById(R.id.weight);
        notes = findViewById(R.id.notes);

        finishButton.setVisibility(View.GONE);
        workout_Details.setVisibility(View.GONE);

        backButton.setOnClickListener(v -> {
            // Navigate to MainActivity and not back to previous
            Intent intent = new Intent(AddExercise.this, SetTrackerPage.class);
            startActivity(intent);
            finish();
        });

        finishButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddExercise.this, SetTrackerPage.class);
            startActivity(intent);
            finish();
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

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                checkIfFinishButtonShouldBeVisible();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        program.addTextChangedListener(textWatcher);
        weight.addTextChangedListener(textWatcher);
        notes.addTextChangedListener(textWatcher);
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

        workout_Details.removeView(addSetButton);
        workout_Details.addView(setView);
        workout_Details.addView(addSetButton);
    }

    private void checkIfFinishButtonShouldBeVisible() {
        if (!program.getText().toString().isEmpty() ||
                !weight.getText().toString().isEmpty() ||
                !notes.getText().toString().isEmpty() ||
                !date.getText().toString().isEmpty() ||
                !time.getText().toString().isEmpty() ||
                !endTime.getText().toString().isEmpty()) {
            finishButton.setVisibility(View.VISIBLE);
        } else {
            finishButton.setVisibility(View.GONE);
        }
    }
}
