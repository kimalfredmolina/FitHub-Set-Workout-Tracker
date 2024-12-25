package com.example.fithub_set_workout_tracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fithub_set_workout_tracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateExercise extends AppCompatActivity {

    private LinearLayout exerciseContainer;
    private Button updateButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private String date, workoutTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_exercise);

        exerciseContainer = findViewById(R.id.exercise_container);
        updateButton = findViewById(R.id.update_btn);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Retrieve data passed from previous page
        date = getIntent().getStringExtra("date");
        workoutTitle = getIntent().getStringExtra("workoutTitle");

        //loadWorkoutDetails();

        //updateButton.setOnClickListener(v -> saveUpdatedData());
    }

    /*private void loadWorkoutDetails() {
        String userId = mAuth.getCurrentUser().getUid();

        // Firebase path: Users -> userId -> workout -> date -> workoutTitle
        databaseReference.child("Users").child(userId)
                .child("workout").child(date.replace("\n", "/"))
                .child(workoutTitle)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot exerciseSnapshot : snapshot.getChildren()) {
                            String exerciseName = exerciseSnapshot.getKey();
                            String bodyWeight = exerciseSnapshot.child("bodyWeight").getValue(String.class);
                            String note = exerciseSnapshot.child("note").getValue(String.class);

                            addExerciseField(exerciseName, bodyWeight, note);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }*/

    /*private void addExerciseField(String exerciseName, String bodyWeight, String note) {
        View exerciseView = getLayoutInflater().inflate(R.layout.exercise_edit_item, exerciseContainer, false);

        TextView exerciseTitle = exerciseView.findViewById(R.id.exercise_title);
        EditText bodyWeightInput = exerciseView.findViewById(R.id.bodyweight_input);
        EditText noteInput = exerciseView.findViewById(R.id.note_input);

        exerciseTitle.setText(exerciseName);
        bodyWeightInput.setText(bodyWeight);
        noteInput.setText(note);

        exerciseContainer.addView(exerciseView);
    }*/

    /*private void saveUpdatedData() {
        String userId = mAuth.getCurrentUser().getUid();

        for (int i = 0; i < exerciseContainer.getChildCount(); i++) {
            View exerciseView = exerciseContainer.getChildAt(i);

            TextView exerciseTitle = exerciseView.findViewById(R.id.exercise_title);
            EditText bodyWeightInput = exerciseView.findViewById(R.id.bodyweight_input);
            EditText noteInput = exerciseView.findViewById(R.id.note_input);

            String exerciseName = exerciseTitle.getText().toString();
            String updatedWeight = bodyWeightInput.getText().toString();
            String updatedNote = noteInput.getText().toString();

            // Update Firebase with new data
            databaseReference.child("Users").child(userId)
                    .child("workout").child(date.replace("\n", "/"))
                    .child(workoutTitle).child(exerciseName)
                    .child("bodyWeight").setValue(updatedWeight);
            databaseReference.child("Users").child(userId)
                    .child("workout").child(date.replace("\n", "/"))
                    .child(workoutTitle).child(exerciseName)
                    .child("note").setValue(updatedNote);
        }
        finish();
    }*/
}
